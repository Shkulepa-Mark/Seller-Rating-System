package com.ratingsystem.repository;

import com.ratingsystem.entity.Comment;
import com.ratingsystem.enums.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllBySellerIdAndStatus(Long sellerId, CommentStatus status);

    List<Comment> findAllByStatus(CommentStatus status);
}
