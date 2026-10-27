package com.ratingsystem.dto.response;

import java.time.LocalDateTime;

public record GameObjectResponse(
        Long id,
        String title,
        String text,
        String gameName,
        String sellerName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}