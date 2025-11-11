package org.springframework.samples.petclinic.notification.domain.port.out;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DiscountOutPort {
    BigDecimal getDiscount(LocalDate startDate, LocalDate endDate);
}
