package io.rappit.services.payment.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.rappit.services.payment.api.json.JsonPayment;
import io.rappit.services.payment.api.json.JsonPaymentStateChangeEvent;

import java.io.IOException;

public class PaymentStateChangeEventDeserializer extends JsonDeserializer<PaymentStateChangeEvent> {

    @Override
    public PaymentStateChangeEvent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return new JsonPaymentStateChangeEvent(p);
    }

}
