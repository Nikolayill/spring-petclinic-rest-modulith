package org.springframework.samples.petclinic.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.samples.petclinic.notification.domain.port.out.NotificationServicePort;
import org.springframework.samples.petclinic.notification.dto.VisitCreatedDto;
import org.springframework.samples.petclinic.owner.domain.event.VisitCreated;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the notification module.
 * Tests the complete flow from event publication to notification sending.
 */
@ApplicationModuleTest(module = "notification",
    mode = ApplicationModuleTest.BootstrapMode.DIRECT_DEPENDENCIES)
@SpringJUnitConfig
class NotificationModuleIntegrationTest {

    @MockitoBean
    private NotificationServicePort notificationServicePort;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Test
    void shouldProcessVisitCreatedEventEndToEnd()
             {
        // Given
        var visitCreated = new VisitCreatedDto(123, LocalDate.now(), "Regular checkup");
        doNothing().when(notificationServicePort).sendNotification(any());

        // When - publish the domain event
        eventPublisher.publishEvent(visitCreated);

        // Then - verify notification was sent (may need to wait for async processing)
        // Note: In Spring Modulith tests, event processing is typically synchronous
        verify(notificationServicePort, timeout(5000)).sendNotification(any());
    }
}
