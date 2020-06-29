package io.rappit.services.payment.api;

import io.rappit.common.media.Media;
import io.rappit.common.media.Printable;
import io.rappit.common.world.finance.Money;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public interface Order extends Printable {
    Long id();

    Long merchant();

    String reference();

    Money amount();

    String description();

    Type type();

    Optional<Recurring> recurring();

    @Override
    default void print(Media media) {
        media.with("id", id())
                .with("reference", reference())
                .with("merchant", merchant())
                .with("amount", amount())
                .with("description", description())
                .with("type", type().name());
        recurring().ifPresent(recurring -> media.with("recurring", recurring));
    }

    enum Type {
        SINGLE,
        OPEN,
        RECURRING,
        INSTALLMENT
    }

    enum Frequency {
        WEEKLY,
        BI_WEEKLY,
        QUAD_WEEKLY,
        MONTHLY,
        SEMI_MONTHLY,
        QUARTERLY,
        SEMI_ANNUALLY,
        ANNUALLY
    }

    interface Recurring extends Printable {
        Frequency frequency();

        Money amount();

        Integer count();

        Optional<LocalDate> start();

        @Override
        default void print(Media media) {
            media.with("frequency", frequency().name())
                    .with("amount", amount())
                    .with("count", count());
            start().ifPresent(start -> media.with("start", start.format(DateTimeFormatter.BASIC_ISO_DATE)));
        }
    }

}
