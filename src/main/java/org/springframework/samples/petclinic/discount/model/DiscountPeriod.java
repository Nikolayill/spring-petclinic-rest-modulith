package org.springframework.samples.petclinic.discount.model;

import java.time.LocalDate;

public record DiscountPeriod(LocalDate startDate, LocalDate endDate) {
}
