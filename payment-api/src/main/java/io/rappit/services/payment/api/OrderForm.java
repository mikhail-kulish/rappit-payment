package io.rappit.services.payment.api;

import io.rappit.common.media.Media;
import io.rappit.common.media.Printable;
import io.rappit.common.world.finance.Money;

import java.util.Collection;
import java.util.Optional;

public interface OrderForm extends Printable {
    Long merchant();

    String reference();

    Optional<Money> amount();

    String description();

    Collection<OrderItem> items();

    Optional<Payer> payer();

    default Order.Type type() {
        return Order.Type.SINGLE;
    }

    Optional<Order.Recurring> recurring();

    @Override
    default void print(Media media) {
        media.with("merchant", merchant())
                .with("reference", reference())
                .with("description", description())
                .with("items", items());
        amount().ifPresent(amount -> media.with("amount", amount));
    }
    
}
