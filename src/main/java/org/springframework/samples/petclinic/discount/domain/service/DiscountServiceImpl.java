package org.springframework.samples.petclinic.discount.domain.service;

import org.springframework.samples.petclinic.discount.domain.DiscountPortOut;
import org.springframework.samples.petclinic.discount.domain.DiscountService;
import org.springframework.samples.petclinic.discount.model.DiscountPeriod;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DiscountServiceImpl implements DiscountService {
    private final DiscountPortOut discountPortOut;

    public DiscountServiceImpl(DiscountPortOut discountPortOut) {
        this.discountPortOut = discountPortOut;
    }

    @Override
    public BigDecimal getDiscount(DiscountPeriod discountPeriod) {
        discountPortOut.incrementCounter();
        return BigDecimal.ZERO;
    }
}
