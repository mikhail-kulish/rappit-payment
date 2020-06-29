package io.rappit.services.payment;

import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.api.PaymentForm;
import io.rappit.services.processor.api.Receipt;

import java.util.Collection;

public interface Payments {
    Payment create(PaymentForm paymentForm);

    Payment get(Long id);

    Payment get(Long id, String customer);

    Payment apply(Receipt receipt);

    Collection<Payment> find(String customer);
}
