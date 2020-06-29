package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Payment;
import org.springframework.context.ApplicationEventPublisher;

import java.util.function.Function;

public class PublishPaymentEvent implements Function<Payment, Payment> {
    private final ApplicationEventPublisher eventPublisher;

    public PublishPaymentEvent(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Payment apply(Payment payment) {
        eventPublisher.publishEvent(payment);
        return payment;
    }
}
