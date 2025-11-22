package com.ratingsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GameObjectRequest(
        @NotBlank String title,
        @NotBlank String text,
        @NotNull Long gameId
) {}