package org.springframework.samples.petclinic.notification.adapter.out.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.notification.domain.model.Notification;
import org.springframework.samples.petclinic.notification.domain.port.out.NotificationServicePort;
import org.springframework.stereotype.Component;

/**
 * Fake REST client implementing NotificationServicePort for external notification service.
 * This is an outbound adapter in the hexagonal architecture.
 * In a real system, this would integrate with actual notification services like email, SMS, etc.
 *
 * @author GitHub Copilot
 */
@Component
public class FakeNotificationServiceClient implements NotificationServicePort {

    private static final Logger logger = LoggerFactory.getLogger(FakeNotificationServiceClient.class);

    @Override
    public void sendNotification(Notification notification) {
        logger.info("Sending notification to external service for visit {} assigned to vet {}",
                   notification.getVisitId(), notification.getVetName());
        // just fake the rest call
    }
}
