package com.ratingsystem.dto.response;

public record LoginResponse(
        String email,
        String token
) {}