package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.notification.NotificationDetailDTO;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.mapper.NotificationMapper;
import com.erick.nutricontrol.model.Notification;
import com.erick.nutricontrol.repository.NotificationRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import com.erick.nutricontrol.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    @Override
    @Transactional
    public NotificationDetailDTO createNotification(User user, Notification notification) {
        notification.setUser(user);
        notification = repository.save(notification);
        return mapper.toDetailDTO(notification);
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

/*public void createAndSendNotification(User user, String message, String type) {
        // 1. Guardamos en la BD usando el User que ya nos pasaron
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .build();

        Notification saved = repository.save(notification);

        // 2. Disparamos la alerta en tiempo real por el WebSocket
        messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/queue/notifications",
                saved // Mandamos el JSON al front
        );
        log.info("Notificación '{}' enviada al usuario ID {}", type, user.getId());
    }*/