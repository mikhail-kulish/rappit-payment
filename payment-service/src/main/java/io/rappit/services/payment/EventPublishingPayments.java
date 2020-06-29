package io.rappit.services.payment;

import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.api.PaymentForm;
import io.rappit.services.processor.api.Receipt;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collection;

public class EventPublishingPayments implements Payments {
    private final ApplicationEventPublisher publisher;
    private final Payments origin;

    public EventPublishingPayments(ApplicationEventPublisher publisher, Payments origin) {
        this.publisher = publisher;
        this.origin = origin;
    }

    @Override
    public Payment create(PaymentForm paymentForm) {
        Payment payment = origin.create(paymentForm);
        publisher.publishEvent(payment);
        return payment;
    }

    @Override
    public Payment get(Long id) {
        return origin.get(id);
    }

    @Override
    public Payment get(Long id, String customer) {
        return origin.get(id, customer);
    }

    @Override
    public Payment apply(Receipt receipt) {
        Payment updated = origin.apply(receipt);
        publisher.publishEvent(updated);
        return updated;
    }

    @Override
    public Collection<Payment> find(String customer) {
        return origin.find(customer);
    }
}
