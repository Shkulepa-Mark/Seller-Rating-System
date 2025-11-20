package com.ratingsystem.controller;

import com.ratingsystem.dto.request.CommentRequest;
import com.ratingsystem.dto.response.CommentResponse;
import com.ratingsystem.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/sellers/{sellerId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<String> addComment(@PathVariable("sellerId") Long sellerId,
                                             @Valid @RequestBody CommentRequest request) {
        commentService.addComment(sellerId, request);
        return ResponseEntity.ok("Comment submitted for moderation.");
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable("sellerId") Long sellerId) {
        return ResponseEntity.ok(commentService.getCommentsForSeller(sellerId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable("sellerId") Long sellerId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentRequest request,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        commentService.updateComment(commentId, request, principal.getName());
        return ResponseEntity.ok("Comment updated and sent for moderation.");
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable("sellerId") Long sellerId,
            @PathVariable("commentId") Long commentId,
            Principal principal
    ) {
        if (principal == null) return ResponseEntity.status(401).body("Unauthorized");

        commentService.deleteComment(commentId, principal.getName());
        return ResponseEntity.ok("Comment deleted.");
    }
}
