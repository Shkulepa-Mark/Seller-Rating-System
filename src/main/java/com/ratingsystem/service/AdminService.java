package com.ratingsystem.service;

import com.ratingsystem.dto.request.RegisterRequest;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
import com.ratingsystem.enums.UserStatus;
import com.ratingsystem.exception.InvalidOperationException;
import com.ratingsystem.exception.ResourceNotFoundException;
import com.ratingsystem.exception.UserAlreadyExistsException;
import com.ratingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createAdmin(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExistsException("Email " + request.email() + " is already in use");
        }

        User admin = new User();
        admin.setFirstName(request.firstName());
        admin.setLastName(request.lastName());
        admin.setEmail(request.email());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setStatus(UserStatus.ACTIVE);

        userRepository.save(admin);
    }

    @Transactional
    public void approveSeller(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + sellerId));

        if (seller.getRole() != Role.ROLE_SELLER) {
            throw new InvalidOperationException("User with id " + sellerId + " is not a seller");
        }

        seller.setStatus(UserStatus.ACTIVE);
        userRepository.save(seller);
    }

    @Transactional
    public void declineSeller(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + sellerId));

        seller.setStatus(UserStatus.DECLINED);
        userRepository.save(seller);
    }
}