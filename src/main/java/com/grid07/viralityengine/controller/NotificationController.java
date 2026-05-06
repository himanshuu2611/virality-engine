package com.grid07.viralityengine.controller;

import com.grid07.viralityengine.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public String sendNotification(
            @RequestParam String userId,
            @RequestParam String message
    ) {

        notificationService.handleBotNotification(userId, message);

        return "Notification Processed";
    }
}