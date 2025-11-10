package org.springframework.samples.petclinic.notification.adapter.in;

import org.springframework.samples.petclinic.notification.NotificationUseCase;
import org.springframework.samples.petclinic.notification.dto.VisitCreatedDto;
import org.springframework.samples.petclinic.owner.domain.event.VisitCreated;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class VisitCreatedListener {
    private final NotificationUseCase useCase;

    public VisitCreatedListener(NotificationUseCase useCase) {
        this.useCase = useCase;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    void on(VisitCreated event) {
        useCase.processVisitCreated(new VisitCreatedDto(event.id(), event.date(), event.description()));
    }
}
