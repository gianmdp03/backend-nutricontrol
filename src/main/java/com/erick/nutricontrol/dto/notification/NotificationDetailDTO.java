package com.erick.nutricontrol.dto.notification;

import com.erick.nutricontrol._enum.NotificationType;

import java.time.OffsetDateTime;

public record NotificationDetailDTO(Long id, String message, NotificationType type, boolean isRead, OffsetDateTime createdAt) {}
