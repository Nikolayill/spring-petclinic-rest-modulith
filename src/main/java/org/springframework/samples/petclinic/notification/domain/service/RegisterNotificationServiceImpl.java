package org.springframework.samples.petclinic.notification.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.notification.domain.RegisterNotificationService;
import org.springframework.samples.petclinic.notification.domain.port.out.DiscountOutPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class RegisterNotificationServiceImpl implements RegisterNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterNotificationServiceImpl.class);
    private final DiscountOutPort  discountOutPort;

    public RegisterNotificationServiceImpl(DiscountOutPort discountOutPort) {
        this.discountOutPort = discountOutPort;
    }

    @Override
    public void register() {
        logger.info("Registering notification service...");
        BigDecimal discount = discountOutPort.getDiscount(LocalDate.now(), LocalDate.now().plusDays(10));
        logger.info("Notification service registered for discount: {}", discount);
    }
}
