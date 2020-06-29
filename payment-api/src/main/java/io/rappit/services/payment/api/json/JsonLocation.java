package io.rappit.services.payment.api.json;

import com.fasterxml.jackson.databind.JsonNode;
import io.rappit.common.world.location.Location;

import java.util.Optional;

public class JsonLocation implements Location {
    private final JsonNode node;

    public JsonLocation(JsonNode node) {
        this.node = node;
    }

    @Override
    public String address() {
        return node.get("address").textValue();
    }

    @Override
    public Optional<String> additional() {
        return Optional.ofNullable(node.get("additional")).map(JsonNode::asText);
    }

    @Override
    public String city() {
        return node.get("city").textValue();
    }

    @Override
    public Optional<String> state() {
        return Optional.ofNullable(node.get("state")).map(JsonNode::asText);
    }

    @Override
    public String postal() {
        return node.get("postal").textValue();
    }

    @Override
    public String country() {
        return node.get("country").textValue();
    }
}
