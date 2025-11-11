package org.springframework.samples.petclinic.notification.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.samples.petclinic.notification.NotificationUseCase;
import org.springframework.samples.petclinic.notification.domain.RegisterNotificationService;
import org.springframework.samples.petclinic.notification.domain.model.Notification;
import org.springframework.samples.petclinic.notification.domain.port.out.NotificationServicePort;
import org.springframework.samples.petclinic.notification.domain.port.out.VetSelectionPort;
import org.springframework.samples.petclinic.notification.dto.VisitCreatedDto;
import org.springframework.samples.petclinic.owner.domain.event.VisitCreated;
import org.springframework.stereotype.Component;

/**
 * Application service implementing notification use cases.
 * This service handles domain events and orchestrates notification workflows.
 *
 * @author GitHub Copilot
 */
@Component
public class NotificationUseCaseImpl implements NotificationUseCase {

    private static final Logger logger = LoggerFactory.getLogger(NotificationUseCaseImpl.class);

    private final RegisterNotificationService registerNotificationService;
    private final VetSelectionPort vetSelectionPort;
    private final NotificationServicePort notificationServicePort;

    public NotificationUseCaseImpl(RegisterNotificationService registerNotificationService,
                                   VetSelectionPort vetSelectionPort,
                                   NotificationServicePort notificationServicePort) {
        this.registerNotificationService = registerNotificationService;
        this.vetSelectionPort = vetSelectionPort;
        this.notificationServicePort = notificationServicePort;
    }

    /**
     * Handles VisitCreated domain events from the owner module.
     * Uses Spring Modulith's ApplicationModuleListener for reliable event processing.
     */
    @ApplicationModuleListener
    @EventListener
    @Override
    public void processVisitCreated(VisitCreatedDto visitCreated) {
        logger.info("Processing visit created event for visit ID: {}", visitCreated.id());

        try {
            // Step 1: Select a random vet
            VetSelectionPort.VetInfo selectedVet = vetSelectionPort.selectRandomVet();

            if (selectedVet == null) {
                logger.warn("No vets available for notification - skipping visit {}", visitCreated.id());
                return;
            }

            logger.info("Selected vet {} (ID: {}) for visit notification",
                       selectedVet.name(), selectedVet.id());

            // Step 2: Create notification
            Notification notification = new Notification(
                visitCreated.id(),
                visitCreated.date(),
                visitCreated.description(),
                selectedVet.id(),
                selectedVet.name()
            );

            // Step 3: Send notification via external service
            notificationServicePort.sendNotification(notification);

            logger.info("Successfully sent notification for visit {} to vet {}",
                       visitCreated.id(), selectedVet.name());
            registerNotificationService.register();
        }  catch (Exception e) {
            logger.error("Unexpected error processing visit created event for visit {}: {}",
                        visitCreated.id(), e.getMessage(), e);
        }
    }
}
