package com.ratingsystem.service;

import com.ratingsystem.dto.request.CommentRequest;
import com.ratingsystem.dto.response.CommentResponse;
import com.ratingsystem.entity.Comment;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.CommentStatus;
import com.ratingsystem.exception.ResourceNotFoundException;
import com.ratingsystem.repository.CommentRepository;
import com.ratingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RatingService ratingService;

    @Transactional
    public void addComment(Long sellerId, CommentRequest request) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + sellerId));

        Comment comment = new Comment();
        comment.setSeller(seller);
        comment.setMessage(request.message());
        comment.setRating(request.rating());
        comment.setStatus(CommentStatus.PENDING);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal();

            String email = userDetails.getUsername();

            Optional<User> author = userRepository.findByEmail(email);
            author.ifPresent(comment::setAuthor);
        } else {
            comment.setAuthor(null);
        }

        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsForSeller(Long sellerId) {
        return commentRepository.findAllBySellerIdAndStatus(sellerId, CommentStatus.APPROVED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void approveComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        comment.setStatus(CommentStatus.APPROVED);
        commentRepository.save(comment);

        ratingService.updateSellerRating(comment.getSeller().getId());
    }

    @Transactional
    public void declineComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        comment.setStatus(CommentStatus.DECLINED);
        commentRepository.save(comment);
    }

    private CommentResponse mapToResponse(Comment c) {
        String authorName = (c.getAuthor() != null)
                ? c.getAuthor().getFirstName() + " " + c.getAuthor().getLastName()
                : "Anonymous User";

        return new CommentResponse(
                c.getId(),
                c.getMessage(),
                c.getRating(),
                authorName,
                c.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getPendingComments() {
        return commentRepository.findAllByStatus(CommentStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequest request, String currentUserEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (comment.getAuthor() == null) {
            throw new AccessDeniedException("Anonymous comments cannot be edited.");
        }

        if (!comment.getAuthor().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You are not the author of this comment.");
        }

        comment.setMessage(request.message());
        comment.setRating(request.rating());

        comment.setStatus(CommentStatus.PENDING);

        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, String currentUserEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (comment.getAuthor() == null) {
            throw new AccessDeniedException("Anonymous comments cannot be deleted.");
        }

        if (!comment.getAuthor().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You are not the author of this comment.");
        }

        commentRepository.delete(comment);

        if (comment.getStatus() == CommentStatus.APPROVED) {
            ratingService.updateSellerRating(comment.getSeller().getId());
        }
    }
}
