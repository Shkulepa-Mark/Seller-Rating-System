package com.ratingsystem.repository;

import com.ratingsystem.entity.GameObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameObjectRepository extends JpaRepository<GameObject, Long> {
}
