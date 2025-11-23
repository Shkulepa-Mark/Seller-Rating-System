package com.ratingsystem.controller;

import com.ratingsystem.dto.request.SellerCreationRequest;
import com.ratingsystem.dto.response.SellerResponse;
import com.ratingsystem.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @GetMapping
    public ResponseEntity<Page<SellerResponse>> getAllSellers(
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "gameName", required = false) String gameName,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(sellerService.getAllSellers(minRating, maxRating, gameName, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerResponse> getSeller(@PathVariable("id") Long id) {
        return ResponseEntity.ok(sellerService.getSellerById(id));
    }

    @PostMapping("/requests")
    public ResponseEntity<String> requestSellerCreation(@Valid @RequestBody SellerCreationRequest request) {
        sellerService.requestSellerCreation(request);
        return ResponseEntity.ok("Request submitted. Administrator will review the profile and comment.");
    }
}