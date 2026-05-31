package com.urbanpark.parking.controller;

import com.urbanpark.parking.domain.Notification;
import com.urbanpark.parking.domain.NotificationType;
import com.urbanpark.parking.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable String condominioId) {
        return ResponseEntity.ok(notificationService.getNotificationsByCondominio(condominioId));
    }

    @PostMapping
    public ResponseEntity<Notification> triggerNotification(
            @RequestParam String condominioId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam NotificationType type) {

        Notification created = notificationService.createNotification(condominioId, title, message, type);
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }
}
