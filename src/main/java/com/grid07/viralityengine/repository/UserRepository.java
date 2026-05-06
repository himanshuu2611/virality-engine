package com.grid07.viralityengine.repository;

import com.grid07.viralityengine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}