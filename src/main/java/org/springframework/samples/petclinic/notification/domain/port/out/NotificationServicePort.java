package org.springframework.samples.petclinic.notification.domain.port.out;

import org.springframework.samples.petclinic.notification.domain.model.Notification;

/**
 * Outbound port for sending notifications to external notification service.
 * This is a driven port in the hexagonal architecture.
 *
 * @author GitHub Copilot
 */
public interface NotificationServicePort {
    void sendNotification(Notification notification);
}
