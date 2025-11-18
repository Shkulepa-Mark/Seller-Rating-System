package com.ratingsystem.service;

import com.ratingsystem.dto.request.LoginRequest;
import com.ratingsystem.dto.response.LoginResponse;
import com.ratingsystem.dto.request.RegisterRequest;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
import com.ratingsystem.enums.UserStatus;
import com.ratingsystem.exception.UserAlreadyExistsException;
import com.ratingsystem.repository.UserRepository;
import com.ratingsystem.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return new LoginResponse(loginRequest.email(), jwt);
    }

    @Transactional
    public void registerSeller(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new UserAlreadyExistsException("Email " + registerRequest.email() + " is already in use");
        }

        User user = new User();
        user.setFirstName(registerRequest.firstName());
        user.setLastName(registerRequest.lastName());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setRole(Role.ROLE_SELLER);
        user.setStatus(UserStatus.PENDING_EMAIL);

        User savedUser = userRepository.save(user);

        String token = UUID.randomUUID().toString();
        String key = "confirm_token:" + token;

        redisTemplate.opsForValue().set(key, savedUser.getId().toString(), 24, TimeUnit.HOURS);

        log.info("=== EMAIL SIMULATION ===");
        log.info("To: {}", user.getEmail());
        log.info("Subject: Confirm Registration");
        log.info("Link: http://localhost:8080/seller-rating-system/auth/confirm?token={}", token);
        log.info("========================");
    }

    @Transactional
    public boolean confirmEmail(String token) {
        String key = "confirm_token:" + token;
        String userIdStr = (String) redisTemplate.opsForValue().get(key);

        if (userIdStr == null) {
            return false;
        }

        Long userId = Long.parseLong(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.PENDING_APPROVAL);
        userRepository.save(user);

        redisTemplate.delete(key);
        return true;
    }
}