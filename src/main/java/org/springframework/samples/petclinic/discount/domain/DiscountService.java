package org.springframework.samples.petclinic.discount.domain;

import org.springframework.samples.petclinic.discount.model.DiscountPeriod;

import java.math.BigDecimal;

public interface DiscountService {
    BigDecimal getDiscount(DiscountPeriod discountPeriod);
}
