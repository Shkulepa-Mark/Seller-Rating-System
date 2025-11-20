package com.ratingsystem.service;

import com.ratingsystem.entity.Comment;
import com.ratingsystem.entity.SellerRating;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.CommentStatus;
import com.ratingsystem.repository.CommentRepository;
import com.ratingsystem.repository.SellerRatingRepository;
import com.ratingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final SellerRatingRepository sellerRatingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void updateSellerRating(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        List<Comment> approvedComments = commentRepository.findAllBySellerIdAndStatus(sellerId, CommentStatus.APPROVED);

        if (approvedComments.isEmpty()) {
            return;
        }

        double average = approvedComments.stream()
                .mapToInt(Comment::getRating)
                .average()
                .orElse(0.0);

        BigDecimal ratingValue = BigDecimal.valueOf(average)
                .setScale(2, RoundingMode.HALF_UP);

        SellerRating rating = sellerRatingRepository.findById(sellerId)
                .orElse(new SellerRating());

        rating.setSeller(seller);
        rating.setRatingValue(ratingValue);

        sellerRatingRepository.save(rating);
    }
}
