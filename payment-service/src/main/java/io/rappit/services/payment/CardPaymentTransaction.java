package io.rappit.services.payment;

import io.rappit.services.account.api.CardAccount;
import io.rappit.services.payment.api.Payment;

import java.util.Optional;

public class CardPaymentTransaction extends PaymentTransaction {
    private final CardAccount account;
    private final String ip;

    public CardPaymentTransaction(Payment payment, CardAccount account, String ip) {
        super(payment);
        this.account = account;
        this.ip = ip;
    }

    @Override
    public CardAccount account() {
        return account;
    }

    @Override
    public Type type() {
        return Type.SALE_TOKEN;
    }

    @Override
    public Optional<String> ip() {
        return Optional.of(ip);
    }

}
