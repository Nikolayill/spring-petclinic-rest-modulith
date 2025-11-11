package org.springframework.samples.petclinic.discount.application;

import org.springframework.samples.petclinic.discount.DiscountUseCase;
import org.springframework.samples.petclinic.discount.domain.DiscountService;
import org.springframework.samples.petclinic.discount.dto.DiscountPeriodDto;
import org.springframework.samples.petclinic.discount.model.DiscountPeriod;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DiscountUseCaseImpl implements DiscountUseCase {
    private final DiscountService discountService;

    public DiscountUseCaseImpl(DiscountService discountService) {
        this.discountService = discountService;
    }

    @Override
    public BigDecimal getDiscount(DiscountPeriodDto period) {
        return discountService.getDiscount(new DiscountPeriod(period.startDate(), period.endDate()));
    }
}
