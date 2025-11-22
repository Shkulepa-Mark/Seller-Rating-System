package com.ratingsystem.controller;

import com.ratingsystem.dto.request.GameObjectRequest;
import com.ratingsystem.dto.response.GameObjectResponse;
import com.ratingsystem.service.GameObjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/game-objects")
@RequiredArgsConstructor
public class GameObjectController {

    private final GameObjectService gameObjectService;

    @GetMapping
    public ResponseEntity< List<GameObjectResponse>> getAll() {
        return ResponseEntity.ok(gameObjectService.getAllGameObjects());
    }

    @PostMapping
    public ResponseEntity<GameObjectResponse> create(@Valid @RequestBody GameObjectRequest request,
                                                     Principal principal) {
        return ResponseEntity.ok(gameObjectService.createGameObject(request, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameObjectResponse> update(@PathVariable("id") Long id,
                                                     @Valid @RequestBody GameObjectRequest request, 
                                                     Principal principal) {
        return ResponseEntity.ok(gameObjectService.updateGameObject(id, request, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id, Principal principal) {
        gameObjectService.deleteGameObject(id, principal.getName());
        return ResponseEntity.ok("Item deleted");
    }
}