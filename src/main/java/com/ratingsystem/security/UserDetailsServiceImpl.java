package com.ratingsystem.security;

import com.ratingsystem.entity.User;
import com.ratingsystem.enums.UserStatus;
import com.ratingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        boolean isEnabled = (user.getStatus() == UserStatus.ACTIVE);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                isEnabled,
                true,
                true,
                true,
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}