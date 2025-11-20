package com.ratingsystem.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequest(
        @NotBlank(message = "Message cannot be empty")
        String message,

        @NotNull(message = "Rating is required")
        @Min(1) @Max(5)
        Integer rating
) {}
