package com.ratingsystem.unit;

import com.ratingsystem.dto.request.CommentRequest;
import com.ratingsystem.entity.Comment;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.CommentStatus;
import com.ratingsystem.exception.ResourceNotFoundException;
import com.ratingsystem.repository.CommentRepository;
import com.ratingsystem.service.CommentService;
import com.ratingsystem.service.RatingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    private static final Long COMMENT_ID = 100L;
    private static final Long SELLER_ID = 2L;
    private static final Long ADMIN_SELLER_ID = 50L;
    private static final String OWNER_EMAIL = "owner@test.com";
    private static final String OTHER_EMAIL = "hacker@test.com";
    private static final String NEW_MESSAGE = "New text";
    private static final int NEW_RATING = 1;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("Should delete comment successfully when user is the author")
    void deleteComment_Success() {
        User author = new User();
        author.setEmail(OWNER_EMAIL);
        User seller = new User();
        seller.setId(SELLER_ID);

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setSeller(seller);
        comment.setStatus(CommentStatus.APPROVED);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        commentService.deleteComment(COMMENT_ID, OWNER_EMAIL);

        verify(commentRepository).delete(comment);
        verify(ratingService).updateSellerRating(seller.getId());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when deleting comment owned by another user")
    void deleteComment_Fail_NotAuthor() {
        User author = new User();
        author.setEmail(OWNER_EMAIL);
        Comment comment = new Comment();
        comment.setAuthor(author);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        assertThrows(AccessDeniedException.class, () ->
                commentService.deleteComment(COMMENT_ID, OTHER_EMAIL)
        );
        verify(commentRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when deleting an anonymous comment")
    void deleteComment_Fail_Anonymous() {
        Comment comment = new Comment();
        comment.setAuthor(null);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        assertThrows(AccessDeniedException.class, () ->
                commentService.deleteComment(COMMENT_ID, OWNER_EMAIL)
        );
    }

    @Test
    @DisplayName("Should update comment successfully and reset status to PENDING")
    void updateComment_Success() {
        User author = new User();
        author.setEmail(OWNER_EMAIL);
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setStatus(CommentStatus.APPROVED);

        CommentRequest request = new CommentRequest(NEW_MESSAGE, NEW_RATING);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        commentService.updateComment(COMMENT_ID, request, OWNER_EMAIL);

        assertEquals(NEW_MESSAGE, comment.getMessage());
        assertEquals(NEW_RATING, comment.getRating());
        assertEquals(CommentStatus.PENDING, comment.getStatus());
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when updating comment owned by another user")
    void updateComment_Fail_NotAuthor() {
        User author = new User();
        author.setEmail(OWNER_EMAIL);
        Comment comment = new Comment();
        comment.setAuthor(author);
        CommentRequest request = new CommentRequest(NEW_MESSAGE, NEW_RATING);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        assertThrows(AccessDeniedException.class, () ->
                commentService.updateComment(COMMENT_ID, request, OTHER_EMAIL)
        );
    }

    @Test
    @DisplayName("Should set status to APPROVED and update seller rating")
    void approveComment_Success() {
        User seller = new User();
        seller.setId(ADMIN_SELLER_ID);
        Comment comment = new Comment();
        comment.setSeller(seller);
        comment.setStatus(CommentStatus.PENDING);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        commentService.approveComment(COMMENT_ID);

        assertEquals(CommentStatus.APPROVED, comment.getStatus());
        verify(commentRepository).save(comment);
        verify(ratingService).updateSellerRating(ADMIN_SELLER_ID);
    }

    @Test
    @DisplayName("Should set status to DECLINED and not update seller rating")
    void declineComment_Success() {
        Comment comment = new Comment();
        comment.setStatus(CommentStatus.PENDING);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        commentService.declineComment(COMMENT_ID);

        assertEquals(CommentStatus.DECLINED, comment.getStatus());
        verify(commentRepository).save(comment);
        verify(ratingService, never()).updateSellerRating(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when comment to approve is not found")
    void approveComment_Fail_NotFound() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commentService.approveComment(COMMENT_ID));
    }
}