package io.rappit.services.payment.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.rappit.services.payment.api.json.JsonPayment;

import java.io.IOException;

public class PaymentDeserializer extends JsonDeserializer<Payment> {

    @Override
    public Payment deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return new JsonPayment(p.getCodec().readTree(p));
    }

}
