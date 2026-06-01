package com.urbanpark.parking.service;

import com.urbanpark.parking.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class NotificationCleanUpService {

    private final NotificationRepository notificationRepository;

    public NotificationCleanUpService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Esto se ejecuta automáticamente todos los días a la medianoche
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanOldNotifications() {
        LocalDateTime limitDate = LocalDateTime.now().minusDays(30);
        
        notificationRepository.deleteByCreatedAtBefore(limitDate);
    }
}

