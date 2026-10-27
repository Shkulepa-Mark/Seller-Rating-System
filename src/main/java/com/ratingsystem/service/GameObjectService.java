package com.ratingsystem.service;

import com.ratingsystem.dto.request.GameObjectRequest;
import com.ratingsystem.dto.response.GameObjectResponse;
import com.ratingsystem.entity.Game;
import com.ratingsystem.entity.GameObject;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
import com.ratingsystem.exception.InvalidOperationException;
import com.ratingsystem.exception.ResourceNotFoundException;
import com.ratingsystem.repository.GameObjectRepository;
import com.ratingsystem.repository.GameRepository;
import com.ratingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameObjectService {

    private final UserRepository userRepository;
    private final GameObjectRepository gameObjectRepository;
    private final GameRepository gameRepository;

    @Transactional
    public GameObjectResponse createGameObject(GameObjectRequest request, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (seller.getRole() != Role.ROLE_SELLER) {
            throw new InvalidOperationException("Only sellers can create game objects");
        }

        Game game = gameRepository.findById(request.gameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game category not found"));

        GameObject gameObject = new GameObject();
        gameObject.setTitle(request.title());
        gameObject.setText(request.text());
        gameObject.setGame(game);
        gameObject.setUser(seller);

        GameObject saved = gameObjectRepository.save(gameObject);
        return mapToResponse(saved);
    }

    @Transactional
    public GameObjectResponse updateGameObject(Long id, GameObjectRequest request, String sellerEmail) {
        GameObject gameObject = gameObjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found"));

        if (!gameObject.getUser().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("You can only edit your own items");
        }

        Game game = gameRepository.findById(request.gameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        gameObject.setTitle(request.title());
        gameObject.setText(request.text());
        gameObject.setGame(game);

        return mapToResponse(gameObjectRepository.save(gameObject));
    }

    @Transactional
    public void deleteGameObject(Long id, String sellerEmail) {
        GameObject gameObject = gameObjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found"));

        if (!gameObject.getUser().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("You can only delete your own items");
        }

        gameObjectRepository.delete(gameObject);
    }

    @Transactional(readOnly = true)
    public List<GameObjectResponse> getAllGameObjects() {
        return gameObjectRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private GameObjectResponse mapToResponse(GameObject go) {
        return new GameObjectResponse(
                go.getId(),
                go.getTitle(),
                go.getText(),
                go.getGame().getName(),
                go.getUser().getFirstName() + " " + go.getUser().getLastName(),
                go.getCreatedAt(),
                go.getUpdatedAt()
        );
    }
}
