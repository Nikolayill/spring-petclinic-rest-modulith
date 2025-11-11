package org.springframework.samples.petclinic.discount;

import org.springframework.samples.petclinic.discount.dto.DiscountPeriodDto;

import java.math.BigDecimal;

public interface DiscountUseCase {
    BigDecimal getDiscount(DiscountPeriodDto period);
}
