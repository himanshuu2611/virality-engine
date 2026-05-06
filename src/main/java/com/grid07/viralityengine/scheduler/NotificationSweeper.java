package com.grid07.viralityengine.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NotificationSweeper {

    private final RedisTemplate<String, Object> redisTemplate;

    // Runs every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void sweepNotifications() {

        System.out.println("Running Notification Sweeper...");

        // Find all pending notification lists
        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");

        if (keys == null || keys.isEmpty()) {
            System.out.println("No pending notifications found");
            return;
        }

        for (String key : keys) {

            // Get all notifications
            List<Object> notifications =
                    redisTemplate.opsForList().range(key, 0, -1);

            if (notifications == null || notifications.isEmpty()) {
                continue;
            }

            int totalNotifications = notifications.size();

            // Get first notification
            String firstNotification = notifications.get(0).toString();

            // Create summarized message
            String summaryMessage;

            if (totalNotifications == 1) {
                summaryMessage =
                        "Summarized Push Notification: "
                                + firstNotification;
            } else {
                summaryMessage =
                        "Summarized Push Notification: "
                                + firstNotification
                                + " and "
                                + (totalNotifications - 1)
                                + " others interacted with your posts.";
            }

            // Print summary
            System.out.println(summaryMessage);

            // Clear Redis list
            redisTemplate.delete(key);

            System.out.println("Cleared notification batch for: " + key);
        }
    }
}