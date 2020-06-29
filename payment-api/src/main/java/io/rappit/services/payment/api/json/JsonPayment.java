package io.rappit.services.payment.api.json;

import com.fasterxml.jackson.databind.JsonNode;
import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.Payer;
import io.rappit.services.payment.api.Payment;

import java.time.Instant;
import java.util.Optional;

public class JsonPayment implements Payment {
    private final JsonNode node;

    public JsonPayment(JsonNode node) {
        this.node = node;
    }

    @Override
    public Long id() {
        return node.get("id").asLong();
    }

    @Override
    public Instant created() {
        return Instant.ofEpochMilli(node.get("created").asLong());
    }

    @Override
    public Optional<Instant> completed() {
        return Optional.ofNullable(node.get("completed"))
                .map(JsonNode::asLong)
                .map(Instant::ofEpochMilli);
    }

    @Override
    public String customer() {
        return node.get("customer").asText();
    }

    @Override
    public Order order() {
        return new JsonOrder(node.get("order"));
    }

    @Override
    public Status status() {
        return Status.valueOf(node.get("status").asText());
    }

    @Override
    public Payer payer() {
        return new JsonPayer(node.get("payer"));
    }
}
