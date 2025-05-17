package com.carpooling.api.dto;

import com.carpooling.api.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private NotificationType type;
    private Long referenceId;
    private boolean read;
    private LocalDateTime createdAt;
}