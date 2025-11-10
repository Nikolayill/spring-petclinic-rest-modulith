package org.springframework.samples.petclinic.notification.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.notification.domain.model.Notification;
import org.springframework.samples.petclinic.notification.domain.port.out.NotificationServicePort;
import org.springframework.samples.petclinic.notification.domain.port.out.VetSelectionPort;
import org.springframework.samples.petclinic.notification.dto.VisitCreatedDto;
import org.springframework.samples.petclinic.owner.domain.event.VisitCreated;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationUseCaseImpl.
 * Tests the core business logic of notification processing.
 *
 * @author GitHub Copilot
 */
@ExtendWith(MockitoExtension.class)
class NotificationUseCaseImplTest {

    @Mock
    private VetSelectionPort vetSelectionPort;

    @Mock
    private NotificationServicePort notificationServicePort;

    private NotificationUseCaseImpl notificationUseCase;

    @BeforeEach
    void setUp() {
        notificationUseCase = new NotificationUseCaseImpl(vetSelectionPort, notificationServicePort);
    }

    @Test
    void shouldProcessVisitCreatedSuccessfully() {
        // Given
        var visitCreated = new VisitCreatedDto(1, LocalDate.now(), "Annual checkup");
        VetSelectionPort.VetInfo vetInfo = new VetSelectionPort.VetInfo(100, "Dr. Smith");

        when(vetSelectionPort.selectRandomVet()).thenReturn(vetInfo);
        doNothing().when(notificationServicePort).sendNotification(any(Notification.class));

        // When
        notificationUseCase.processVisitCreated(visitCreated);

        // Then
        verify(vetSelectionPort).selectRandomVet();
        verify(notificationServicePort).sendNotification(any(Notification.class));
    }

    @Test
    void shouldSkipNotificationWhenNoVetsAvailable() {
        // Given
        var visitCreated = new VisitCreatedDto(1, LocalDate.now(), "Annual checkup");

        when(vetSelectionPort.selectRandomVet()).thenReturn(null);

        // When
        notificationUseCase.processVisitCreated(visitCreated);

        // Then
        verify(vetSelectionPort).selectRandomVet();
        verify(notificationServicePort, never()).sendNotification(any(Notification.class));
    }

    @Test
    void shouldHandleVetSelectionFailureGracefully() {
        // Given
        var visitCreated = new VisitCreatedDto(1, LocalDate.now(), "Annual checkup");

        when(vetSelectionPort.selectRandomVet()).thenThrow(new RuntimeException("Database error"));

        // When - should not throw exception
        notificationUseCase.processVisitCreated(visitCreated);

        // Then
        verify(vetSelectionPort).selectRandomVet();
        verify(notificationServicePort, never()).sendNotification(any(Notification.class));
    }
}
