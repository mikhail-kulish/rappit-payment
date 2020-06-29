package io.rappit.services.payment.api.json;

import com.fasterxml.jackson.databind.JsonNode;
import io.rappit.common.world.location.Location;
import io.rappit.common.world.people.Name;
import io.rappit.services.payment.api.Payer;

import java.util.Optional;

public class JsonPayer implements Payer {
    private final JsonNode node;

    public JsonPayer(JsonNode node) {
        this.node = node;
    }

    @Override
    public String email() {
        return node.get("email").textValue();
    }

    @Override
    public Name name() {
        return new Name.Simple(node.get("name").get("first").asText(), node.get("name").get("last").asText());
    }

    @Override
    public Optional<Location> billing() {
        return Optional.ofNullable(node.get("billing")).map(JsonLocation::new);
    }
}

