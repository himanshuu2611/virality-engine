package com.grid07.viralityengine.repository;

import com.grid07.viralityengine.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}