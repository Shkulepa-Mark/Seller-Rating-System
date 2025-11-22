package com.ratingsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank String code,
        @NotBlank @Size(min = 8, max = 50) String newPassword
) {}