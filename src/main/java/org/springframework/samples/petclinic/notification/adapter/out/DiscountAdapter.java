package org.springframework.samples.petclinic.notification.adapter.out;

import org.springframework.samples.petclinic.discount.DiscountUseCase;
import org.springframework.samples.petclinic.discount.dto.DiscountPeriodDto;
import org.springframework.samples.petclinic.notification.domain.port.out.DiscountOutPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DiscountAdapter implements DiscountOutPort {
    private final DiscountUseCase discountUseCase;

    public DiscountAdapter(DiscountUseCase discountUseCase) {
        this.discountUseCase = discountUseCase;
    }

    @Override
    public BigDecimal getDiscount(LocalDate startDate, LocalDate endDate) {
        return discountUseCase.getDiscount(new DiscountPeriodDto(startDate, endDate));
    }
}
