package io.rappit.services.payment.command;

import io.rappit.services.payment.CardPaymentTransaction;

import java.util.function.Function;

public class ValidateCardPaymentTransaction implements Function<CardPaymentTransaction, CardPaymentTransaction> {
    @Override
    public CardPaymentTransaction apply(CardPaymentTransaction cardPaymentTransaction) {
        return cardPaymentTransaction;
    }
}
