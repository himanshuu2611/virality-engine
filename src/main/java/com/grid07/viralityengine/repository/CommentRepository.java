package com.grid07.viralityengine.repository;

import com.grid07.viralityengine.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}