package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.notification.NotificationDetailDTO;
import com.erick.nutricontrol.mapper.NotificationMapper;
import com.erick.nutricontrol.model.Notification;
import com.erick.nutricontrol.repository.NotificationRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repository;
    private final NotificationMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public NotificationDetailDTO createNotification(User user, Notification notification) {
        notification.setUser(user);
        Notification saved = repository.save(notification);
        NotificationDetailDTO dto = mapper.toDetailDTO(notification);
        messagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/notifications", dto);
        log.info("Notificación enviada vía WebSocket al usuario ID: {}", user.getId());
        return dto;
    }

    @Override
    public Page<NotificationDetailDTO> listUserNotifications(User user, Pageable pageable) {
        Page<Notification> page = repository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user, pageable);
        if(page.isEmpty()){
            return Page.empty();
        }
        return page.map(mapper::toDetailDTO);
    }

    @Override
    @Transactional
    public void markAsRead(User user, Long id) {
        int updated = repository.markAsRead(id, user.getId());
        if (updated > 0) {
            log.info("Notificación {} marcada como leída por el usuario {}", id, user.getId());
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        int updatedCount = repository.markAllAsReadByUserId(user.getId());
        log.info("Acción de usuario: Se marcaron como leídas {} notificaciones del usuario ID: {}", updatedCount, user.getId());
    }

    @Override
    @Transactional
    public void deleteNotification(User user, Long id) {
        repository.deleteByIdAndUser(id, user);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(User user) {
        int deletedCount = repository.deleteAllByUserId(user.getId());
        log.info("Acción de usuario: Se eliminaron por completo las {} notificaciones del usuario ID: {}", deletedCount, user.getId());
    }
}