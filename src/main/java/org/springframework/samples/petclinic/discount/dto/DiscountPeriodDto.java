package org.springframework.samples.petclinic.discount.dto;

import java.time.LocalDate;

public record DiscountPeriodDto(LocalDate startDate, LocalDate endDate) {
}
