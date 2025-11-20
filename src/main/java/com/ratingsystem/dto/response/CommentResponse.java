package com.ratingsystem.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String message,
        Integer rating,
        String authorName,
        LocalDateTime createdAt
) {}
