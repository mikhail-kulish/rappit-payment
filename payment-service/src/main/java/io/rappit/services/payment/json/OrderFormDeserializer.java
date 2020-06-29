package io.rappit.services.payment.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.rappit.services.payment.api.OrderForm;
import io.rappit.services.payment.api.json.JsonOrderForm;

import java.io.IOException;

public class OrderFormDeserializer extends JsonDeserializer<OrderForm> {
    @Override
    public OrderForm deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new JsonOrderForm(p.getCodec().readTree(p));
    }
}
