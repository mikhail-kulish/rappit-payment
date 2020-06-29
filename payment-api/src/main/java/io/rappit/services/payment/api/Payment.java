package io.rappit.services.payment.api;

import io.rappit.common.media.Media;
import io.rappit.common.media.Printable;

import java.time.Instant;
import java.util.Optional;

public interface Payment extends Printable {
    Long id();

    Instant created();

    Optional<Instant> completed();

    String customer();

    Order order();

    Status status();

    Payer payer();

    @Override
    default void print(Media media) {
        media.with("id", id())
                .with("created", created().toEpochMilli())
                .with("order", order())
                .with("customer", customer())
                .with("status", status().name())
                .with("payer", payer());
        completed().ifPresent(completed -> media.with("completed", completed.toEpochMilli()));
    }

    enum Status {
        PENDING,
        PROCESSING,
        APPROVED,
        DECLINED,
        SUSPENDED,
        ERROR
    }
}
