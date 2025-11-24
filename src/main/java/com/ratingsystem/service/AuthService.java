package com.ratingsystem.service;

import com.ratingsystem.dto.request.ForgotPasswordRequest;
import com.ratingsystem.dto.request.LoginRequest;
import com.ratingsystem.dto.request.ResetPasswordRequest;
import com.ratingsystem.dto.response.LoginResponse;
import com.ratingsystem.dto.request.RegisterRequest;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
import com.ratingsystem.enums.UserStatus;
import com.ratingsystem.exception.InvalidOperationException;
import com.ratingsystem.exception.ResourceNotFoundException;
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
    private final EmailService emailService;

    private static final long CONFIRMATION_LINK_TTL_HOURS = 24;
    private static final long RESET_CODE_TTL_HOURS = 1;
    private static final String CONFIRM_KEY_PREFIX = "confirm_token:";
    private static final String RESET_KEY_PREFIX = "reset_token:";

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
        String key = CONFIRM_KEY_PREFIX + token;

        redisTemplate.opsForValue().set(key, savedUser.getId().toString(), CONFIRMATION_LINK_TTL_HOURS, TimeUnit.HOURS);

        String link = "http://localhost:8080/seller-rating-system/auth/confirm?token=" + token;
        String subject = "Confirm your Seller Registration";
        String message = "Hello " + user.getFirstName() + "!\n\n" +
                "Please click the link below to confirm your email:\n" +
                link + "\n\n" +
                "This link will expire in 24 hours.";

        emailService.sendEmail(user.getEmail(), subject, message);

        log.info("Verification email sent to {}", user.getEmail());
    }

    @Transactional
    public boolean confirmEmail(String token) {
        String key = CONFIRM_KEY_PREFIX + token;
        String userIdStr = (String) redisTemplate.opsForValue().get(key);

        if (userIdStr == null) {
            return false;
        }

        Long userId = Long.parseLong(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for activation"));

        user.setStatus(UserStatus.PENDING_APPROVAL);
        userRepository.save(user);

        redisTemplate.delete(key);
        return true;
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String code = UUID.randomUUID().toString();
        String key = RESET_KEY_PREFIX + code;

        redisTemplate.opsForValue().set(key, user.getId().toString(), RESET_CODE_TTL_HOURS, TimeUnit.HOURS);

        String subject = "Password Reset Request";
        String message = "Hello " + user.getFirstName() + ",\n\n" +
                "You requested to reset your password.\n" +
                "Your reset code is: " + code + "\n\n" +
                "This code will expire in 1 hour.";

        emailService.sendEmail(user.getEmail(), subject, message);

        log.info("Reset code sent to {}", user.getEmail());
    }

    public boolean checkResetCode(String code) {
        String key = RESET_KEY_PREFIX + code;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String key = RESET_KEY_PREFIX + request.code();
        String userIdStr = (String) redisTemplate.opsForValue().get(key);

        if (userIdStr == null) {
            throw new InvalidOperationException("Invalid or expired reset code");
        }

        Long userId = Long.parseLong(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        redisTemplate.delete(key);
    }
}