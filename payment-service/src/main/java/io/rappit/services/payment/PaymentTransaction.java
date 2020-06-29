package io.rappit.services.payment;

import io.rappit.common.world.finance.Money;
import io.rappit.common.world.location.Location;
import io.rappit.common.world.people.Name;
import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.Payment;
import io.rappit.services.processor.api.Transaction;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class PaymentTransaction implements Transaction {
    private static final Random RND = new SecureRandom();
    private final String id;
    private final Payment payment;

    public PaymentTransaction(Payment payment) {
        this.id = RND.ints(16, 'a', 'z' + 1)
                .boxed()
                .map(
                        i -> String.valueOf((char) i.byteValue())
                ).collect(Collectors.joining());
        this.payment = payment;
    }

    public Payment payment() {
        return payment;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String reference() {
        return payment.id().toString();
    }

    @Override
    public Money amount() {
        return order().amount();
    }

    @Override
    public Order order() {
        return payment.order();
    }

    @Override
    public Optional<Name> name() {
        return Optional.of(payment.payer().name());
    }

    @Override
    public Optional<String> email() {
        return Optional.of(payment.payer().email());
    }

    @Override
    public Optional<Location> billing() {
        return payment.payer().billing();
    }

}
