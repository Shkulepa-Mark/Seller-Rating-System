package com.ratingsystem.dto.response;

import java.time.LocalDateTime;

public record SellerResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Double rating,
        LocalDateTime registrationDate
) {}