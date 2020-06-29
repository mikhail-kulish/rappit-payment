package io.rappit.services.payment;

import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.api.PaymentForm;

public interface PaymentsList extends Iterable<Payment> {
    Payment create(PaymentForm paymentForm);

}
