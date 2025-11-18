package com.ratingsystem.controller;

import com.ratingsystem.dto.request.LoginRequest;
import com.ratingsystem.dto.request.RegisterRequest;
import com.ratingsystem.dto.response.LoginResponse;
import com.ratingsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.registerSeller(registerRequest);
        return ResponseEntity.ok("Registration successful. Check logs for confirmation link.");
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        if (authService.confirmEmail(token)) {
            return ResponseEntity.ok("Email confirmed. Wait for admin approval.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }
}