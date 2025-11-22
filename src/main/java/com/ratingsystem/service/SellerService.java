package com.ratingsystem.service;

import com.ratingsystem.dto.response.SellerResponse;
import com.ratingsystem.entity.GameObject;
import com.ratingsystem.entity.SellerRating;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
import com.ratingsystem.enums.UserStatus;
import com.ratingsystem.exception.ResourceNotFoundException;
import com.ratingsystem.repository.UserRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<SellerResponse> getAllSellers(Double minRating, Double maxRating, String gameName, Pageable pageable) {

        boolean isRatingSort = pageable.getSort().stream()
                .anyMatch(o -> o.getProperty().equals("rating") || o.getProperty().equals("rating.ratingValue"));

        Pageable effectivePageable = pageable;

        if (isRatingSort) {
            List<Sort.Order> otherOrders = pageable.getSort().stream()
                    .filter(o -> !o.getProperty().startsWith("rating"))
                    .toList();
            effectivePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(otherOrders));
        }

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("role"), Role.ROLE_SELLER));
            predicates.add(cb.equal(root.get("status"), UserStatus.ACTIVE));

            Join<User, SellerRating> ratingJoin = root.join("rating", JoinType.LEFT);

            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(ratingJoin.get("ratingValue"), BigDecimal.valueOf(minRating)));
            }
            if (maxRating != null) {
                predicates.add(cb.lessThanOrEqualTo(ratingJoin.get("ratingValue"), BigDecimal.valueOf(maxRating)));
            }

            if (gameName != null && !gameName.isBlank()) {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<GameObject> subRoot = sub.from(GameObject.class);
                sub.select(subRoot.get("id"));
                sub.where(
                        cb.equal(subRoot.get("user"), root),
                        cb.equal(subRoot.join("game").get("name"), gameName)
                );
                predicates.add(cb.exists(sub));
            }

            if (isRatingSort) {
                Expression<Number> sortExpr = cb.coalesce(ratingJoin.get("ratingValue"), 0.0);

                Sort.Order order = pageable.getSort().getOrderFor("rating");
                if (order == null) order = pageable.getSort().getOrderFor("rating.ratingValue");

                if (order != null && order.isDescending()) {
                    query.orderBy(cb.desc(sortExpr));
                } else {
                    query.orderBy(cb.asc(sortExpr));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, effectivePageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public SellerResponse getSellerById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + id));

        if (user.getRole() != Role.ROLE_SELLER || user.getStatus() != UserStatus.ACTIVE) {
            throw new ResourceNotFoundException("Seller not found or not active");
        }

        return mapToResponse(user);
    }

    private SellerResponse mapToResponse(User u) {
        double rating = u.getRating() != null && u.getRating().getRatingValue() != null
                ? u.getRating().getRatingValue().doubleValue()
                : 0.0;

        return new SellerResponse(
                u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                rating,
                u.getCreatedAt()
        );
    }
}