package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.Notification;
import com.urbanpark.parking.domain.NotificationTemplate;
import com.urbanpark.parking.domain.NotificationType;
import com.urbanpark.parking.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(String condominioId, String title, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setCondominioId(condominioId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public Notification createTemplateNotification(String condominioId, NotificationTemplate template,
            NotificationType type, String param) {
        Notification notification = new Notification();
        notification.setCondominioId(condominioId);
        notification.setTitle(template.getTitle());
        notification.setMessage(template.formatMessage(param));
        notification.setType(type);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByCondominio(String condominioId) {
        return notificationRepository.findByCondominioIdOrderByCreatedAtDesc(condominioId);
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(
                        () -> new IllegalArgumentException("No se encontró la notificación con ID: " + notificationId));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
