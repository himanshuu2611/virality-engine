package com.grid07.viralityengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void handleBotNotification(String userId, String notificationMessage) {

        // Cooldown key
        String cooldownKey = "user:" + userId + ":notif_cooldown";

        // Pending notification list
        String pendingNotifKey = "user:" + userId + ":pending_notifs";

        // Check cooldown
        Boolean hasCooldown = redisTemplate.hasKey(cooldownKey);

        if (Boolean.TRUE.equals(hasCooldown)) {

            // Add notification to Redis List
            redisTemplate.opsForList()
                    .rightPush(pendingNotifKey, notificationMessage);

            System.out.println("Notification added to batch list");

        } else {

            // Send instant notification
            System.out.println("Push Notification Sent to User");

            // Set 15 minute cooldown
            redisTemplate.opsForValue()
                    .set(cooldownKey, "active", Duration.ofMinutes(15));
        }
    }
}