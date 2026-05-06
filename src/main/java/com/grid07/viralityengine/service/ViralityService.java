package com.grid07.viralityengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViralityService {

    private final StringRedisTemplate redisTemplate;


    public void incrementViralityScore(
            Long postId,
            int points
    ) {

        String key =
                "post:" + postId + ":virality_score";

        redisTemplate.opsForValue()
                .increment(key, points);
    }


    public String getViralityScore(Long postId) {

        String key =
                "post:" + postId + ":virality_score";

        return redisTemplate.opsForValue()
                .get(key);
    }
}