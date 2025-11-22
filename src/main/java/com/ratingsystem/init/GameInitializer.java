package com.ratingsystem.init;

import com.ratingsystem.entity.Game;
import com.ratingsystem.repository.GameRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameInitializer {

    private final GameRepository gameRepository;

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void initGames() {
        if (gameRepository.count() > 0) {
            log.info("Games already initialized.");
            return;
        }

        log.info("Initializing default games...");

        List<String> gameNames = List.of(
                "Counter-Strike 2",
                "Dota 2",
                "FIFA 24",
                "Team Fortress 2",
                "Minecraft",
                "Rust",
                "Valorant"
        );

        for (String name : gameNames) {
            Game game = new Game();
            game.setName(name);
            gameRepository.save(game);
        }

        log.info("Default games created: {}", gameNames);
    }
}
