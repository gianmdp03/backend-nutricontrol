package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.notification.NotificationDetailDTO;
import com.erick.nutricontrol.model.Notification;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    NotificationDetailDTO createNotification(User user, Notification notification);
    Page<NotificationDetailDTO> listUserNotifications(User user, Pageable pageable);
    void deleteNotification(User user, Long id);
    void deleteAllNotifications(User user);

}
