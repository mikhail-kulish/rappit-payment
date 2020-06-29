package io.rappit.services.payment.api.json;

import com.fasterxml.jackson.core.JsonParser;
import io.rappit.common.event.gcp.JacksonEvent;
import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.api.PaymentStateChangeEvent;

import java.io.IOException;

public class JsonPaymentStateChangeEvent extends JacksonEvent implements PaymentStateChangeEvent {
    public JsonPaymentStateChangeEvent(JsonParser parser) throws IOException {
        super(parser);
    }

    @Override
    public Payment payment() {
        try {
            return data().readValueAs(Payment.class);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
