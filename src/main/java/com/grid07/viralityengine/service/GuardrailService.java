package com.grid07.viralityengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class GuardrailService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final int MAX_COMMENTS = 100;

    // HORIZONTAL CAP
    public void checkHorizontalCap(Long postId) {

        String key = "post:" + postId + ":bot_count";

        // Atomic increment
        Long count = redisTemplate.opsForValue().increment(key);

        // Set expiry only first time
        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofHours(24));
        }

        // Block after limit
        if (count != null && count > MAX_COMMENTS) {

            // rollback increment
            redisTemplate.opsForValue().decrement(key);

            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Bot reply limit exceeded for this post"
            );
        }
    }

    // VERTICAL CAP
    public void checkVerticalCap(int depthLevel) {

        if (depthLevel > 20) {

            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Comment thread depth exceeded"
            );
        }
    }

    // COOLDOWN CAP
    public void checkCooldownCap(
            String botId,
            String humanId
    ) {

        String key =
                "cooldown:bot_" + botId +
                        ":human_" + humanId;

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(
                        key,
                        "locked",
                        Duration.ofMinutes(10)
                );

        if (Boolean.FALSE.equals(success)) {

            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Cooldown active between bot and human"
            );
        }
    }

    // MAIN VALIDATOR
    public void validateBotReply(
            Long postId,
            int depthLevel,
            String botId,
            String humanId
    ) {

        checkHorizontalCap(postId);

        checkVerticalCap(depthLevel);

        checkCooldownCap(botId, humanId);
    }
}