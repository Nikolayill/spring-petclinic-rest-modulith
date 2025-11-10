package org.springframework.samples.petclinic.notification.dto;

import java.time.LocalDate;

public record VisitCreatedDto(
    Integer id,
    LocalDate date,
    String description
) {
}
