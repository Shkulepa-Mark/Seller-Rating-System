package com.ratingsystem.init;

import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
import com.ratingsystem.enums.UserStatus;
import com.ratingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void initDefaultAdmin() {
        String adminEmail = "admin@rating.com";

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.info("Default admin already exists.");
            return;
        }

        log.info("Creating default admin user...");

        User admin = new User();
        admin.setFirstName("Super");
        admin.setLastName("Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setStatus(UserStatus.ACTIVE);

        userRepository.save(admin);

        log.info("Default admin created. Login: '{}', Password: 'admin123'", adminEmail);
    }
}