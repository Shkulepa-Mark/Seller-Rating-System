package com.ratingsystem.repository;

import com.ratingsystem.entity.User;
import com.ratingsystem.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    List<User> findAllByStatus(UserStatus status);
}
