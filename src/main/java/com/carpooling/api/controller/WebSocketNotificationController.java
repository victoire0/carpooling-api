package com.carpooling.api.controller;

import com.carpooling.api.dto.NotificationDTO;
import com.carpooling.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebSocketNotificationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;  //pour gérer les notifications

    @MessageMapping("/notification/read/{notificationId}")
    public void markNotificationAsRead(Long notificationId, String userId) {
        // Marquer la notification comme lue
        NotificationDTO updatedNotification = notificationService.markAsRead(notificationId);
        
        // Notification aux clients connectés
        messagingTemplate.convertAndSendToUser(
            userId,
            "/topic/notifications/status",
            Map.of("id", notificationId, "read", true)
        );
    }
}