package io.rappit.services.payment.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.rappit.services.payment.api.json.JsonOrderItem;
import io.rappit.services.payment.api.json.JsonPayer;

import java.io.IOException;

public class PayerDeserializer extends JsonDeserializer<Payer> {

    @Override
    public Payer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return new JsonPayer(p.getCodec().readTree(p));
    }

}
