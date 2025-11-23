package com.ratingsystem.controller;

import com.ratingsystem.dto.response.GameResponse;
import com.ratingsystem.entity.Game;
import com.ratingsystem.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameRepository gameRepository;

    @GetMapping
    public ResponseEntity<List<GameResponse>> getAllGames() {
        List<GameResponse> games = gameRepository.findAll().stream()
                .map(game -> new GameResponse(game.getId(), game.getName()))
                .toList();

        return ResponseEntity.ok(games);
    }
}