package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.notification.NotificationDetailDTO;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'ROLE_ADMIN')")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/unread")
    public ResponseEntity<Page<NotificationDetailDTO>> getUnreadNotifications(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return ResponseEntity.ok(notificationService.listUserNotifications(user, pageable));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@AuthenticationPrincipal User user, @PathVariable Long id) {
        notificationService.markAsRead(user, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        notificationService.deleteNotification(user, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<Void> deleteAllNotifications(@AuthenticationPrincipal User user) {
        notificationService.deleteAllNotifications(user);
        return ResponseEntity.noContent().build();
    }
}