package io.rappit.services.payment.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.rappit.services.payment.api.json.JsonOrderItem;

import java.io.IOException;

public class OrderItemDeserializer extends JsonDeserializer<OrderItem> {

    @Override
    public OrderItem deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return new JsonOrderItem(p.getCodec().readTree(p));
    }

}
