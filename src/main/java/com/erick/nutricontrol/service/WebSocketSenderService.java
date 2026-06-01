package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.notification.NotificationDetailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketSenderService {
  private final SimpMessagingTemplate messagingTemplate;

  @Async
  public void sendNotificationAsync(String username, NotificationDetailDTO dto) {
    try {
      messagingTemplate.convertAndSendToUser(username, "/queue/notifications", dto);
      log.info("Notificación enviada asíncronamente vía WebSocket al usuario: {}", username);
    } catch (Exception e) {
      log.error("Fallo al enviar notificación por WebSocket al usuario {}", username, e);
    }
  }
}
