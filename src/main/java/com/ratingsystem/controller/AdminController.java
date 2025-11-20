package com.ratingsystem.controller;

import com.ratingsystem.dto.request.RegisterRequest;
import com.ratingsystem.dto.response.CommentResponse;
import com.ratingsystem.service.AdminService;
import com.ratingsystem.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final CommentService commentService;

    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(@Valid @RequestBody RegisterRequest request) {
        adminService.createAdmin(request);
        return ResponseEntity.ok("New Admin created successfully");
    }

    @PostMapping("/users/{id}/approve")
    public ResponseEntity<String> approveSeller(@PathVariable("id") Long id) {
        adminService.approveSeller(id);
        return ResponseEntity.ok("Seller account approved. They can now log in.");
    }

    @PostMapping("/users/{id}/decline")
    public ResponseEntity<String> declineSeller(@PathVariable("id") Long id) {
        adminService.declineSeller(id);
        return ResponseEntity.ok("Seller registration declined.");
    }

    @GetMapping("/comments/pending")
    public ResponseEntity<List<CommentResponse>> getPendingComments() {
        return ResponseEntity.ok(commentService.getPendingComments());
    }

    @PostMapping("/comments/{id}/approve")
    public ResponseEntity<String> approveComment(@PathVariable("id") Long id) {
        commentService.approveComment(id);
        return ResponseEntity.ok("Comment approved and rating updated.");
    }

    @PostMapping("/comments/{id}/decline")
    public ResponseEntity<String> declineComment(@PathVariable("id") Long id) {
        commentService.declineComment(id);
        return ResponseEntity.ok("Comment declined.");
    }
}