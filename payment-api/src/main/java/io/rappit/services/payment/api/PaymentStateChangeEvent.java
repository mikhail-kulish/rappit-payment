package io.rappit.services.payment.api;

import io.rappit.common.event.Event;
import io.rappit.common.media.Media;

public interface PaymentStateChangeEvent extends Event {
    Payment payment();

    @Override
    default void print(Media media) {
        Event.super.print(media);
        media.with("payment", payment());
    }

}
