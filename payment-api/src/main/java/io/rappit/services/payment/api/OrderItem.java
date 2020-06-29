package io.rappit.services.payment.api;

import io.rappit.common.media.Media;
import io.rappit.common.media.Printable;
import io.rappit.common.world.finance.Money;

import java.util.Optional;

public interface OrderItem extends Printable {
    Money price();

    Integer quantity();

    Optional<String> sku();

    String description();

    Optional<Money> tax();

    @Override
    default void print(Media media) {
        media.with("price", price())
                .with("description", description());
        sku().ifPresent(sku -> media.with("sku", sku));
        tax().ifPresent(tax -> media.with("tax", tax));
    }
}
