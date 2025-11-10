package org.springframework.samples.petclinic.owner.domain.event;

import java.time.LocalDate;

public record VisitCreated(
    Integer id,
    LocalDate date,
    String description
) {
}
