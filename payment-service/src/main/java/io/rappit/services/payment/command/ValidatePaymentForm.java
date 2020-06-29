package io.rappit.services.payment.command;

import io.rappit.services.payment.api.PaymentForm;

import java.util.function.Function;

public class ValidatePaymentForm implements Function<PaymentForm, PaymentForm> {
    @Override
    public PaymentForm apply(PaymentForm paymentForm) {
        if (paymentForm.payer() == null) {
            throw new IllegalArgumentException("Payer information should be set");
        }
        return paymentForm;
    }
}
