package com.ratingsystem.controller;

import com.ratingsystem.dto.request.RegisterRequest;
import com.ratingsystem.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<String> createAdmin(@Valid @RequestBody RegisterRequest request) {
        adminService.createAdmin(request);
        return ResponseEntity.ok("New Admin created successfully");
    }
}