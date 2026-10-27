package com.ratingsystem.repository;

import com.ratingsystem.entity.SellerRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRatingRepository extends JpaRepository<SellerRating, Long> {
}
