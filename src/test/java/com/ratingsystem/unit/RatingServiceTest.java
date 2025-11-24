package com.ratingsystem.unit;

import com.ratingsystem.entity.Comment;
import com.ratingsystem.entity.SellerRating;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.CommentStatus;
import com.ratingsystem.repository.CommentRepository;
import com.ratingsystem.repository.SellerRatingRepository;
import com.ratingsystem.repository.UserRepository;
import com.ratingsystem.service.RatingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    private static final Long SELLER_ID = 1L;
    private static final Long NON_EXISTENT_SELLER_ID = 999L;
    private static final int RATING_FIVE = 5;
    private static final int RATING_FOUR = 4;
    private static final String EXPECTED_AVG_RATING = "4.50";
    private static final String EXPECTED_UPDATED_RATING = "5.00";

    @Mock
    private SellerRatingRepository sellerRatingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    @DisplayName("Should calculate average rating correctly")
    void updateSellerRating_ShouldCalculateAverage() {
        User seller = new User();
        seller.setId(SELLER_ID);
        Comment c1 = new Comment();
        c1.setRating(RATING_FIVE);
        Comment c2 = new Comment();
        c2.setRating(RATING_FOUR);

        when(userRepository.findById(SELLER_ID)).thenReturn(Optional.of(seller));
        when(commentRepository.findAllBySellerIdAndStatus(SELLER_ID, CommentStatus.APPROVED))
                .thenReturn(List.of(c1, c2));
        when(sellerRatingRepository.findById(SELLER_ID)).thenReturn(Optional.empty());

        ratingService.updateSellerRating(SELLER_ID);

        ArgumentCaptor<SellerRating> captor = ArgumentCaptor.forClass(SellerRating.class);
        verify(sellerRatingRepository).save(captor.capture());
        assertEquals(new BigDecimal(EXPECTED_AVG_RATING), captor.getValue().getRatingValue());
    }

    @Test
    @DisplayName("Should throw exception when seller is not found")
    void updateSellerRating_ShouldThrow_WhenSellerNotFound() {
        when(userRepository.findById(NON_EXISTENT_SELLER_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ratingService.updateSellerRating(NON_EXISTENT_SELLER_ID));

        verify(sellerRatingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not save rating when comment list is empty")
    void updateSellerRating_ShouldDoNothing_WhenNoComments() {
        User seller = new User();
        seller.setId(SELLER_ID);

        when(userRepository.findById(SELLER_ID)).thenReturn(Optional.of(seller));
        when(commentRepository.findAllBySellerIdAndStatus(SELLER_ID, CommentStatus.APPROVED))
                .thenReturn(List.of());

        ratingService.updateSellerRating(SELLER_ID);

        verify(sellerRatingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update existing rating record instead of creating a new one")
    void updateSellerRating_ShouldUpdateExisting() {
        User seller = new User();
        seller.setId(SELLER_ID);
        Comment c1 = new Comment();
        c1.setRating(RATING_FIVE);

        SellerRating existingRating = new SellerRating();
        existingRating.setSeller(seller);
        existingRating.setRatingValue(BigDecimal.ZERO);

        when(userRepository.findById(SELLER_ID)).thenReturn(Optional.of(seller));
        when(commentRepository.findAllBySellerIdAndStatus(SELLER_ID, CommentStatus.APPROVED))
                .thenReturn(List.of(c1));
        when(sellerRatingRepository.findById(SELLER_ID)).thenReturn(Optional.of(existingRating));

        ratingService.updateSellerRating(SELLER_ID);

        verify(sellerRatingRepository).save(existingRating);
        assertEquals(new BigDecimal(EXPECTED_UPDATED_RATING), existingRating.getRatingValue());
    }
}