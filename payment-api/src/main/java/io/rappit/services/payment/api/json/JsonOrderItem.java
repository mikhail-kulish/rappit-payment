package io.rappit.services.payment.api.json;

import com.fasterxml.jackson.databind.JsonNode;
import io.rappit.common.world.finance.Money;
import io.rappit.services.payment.api.OrderItem;

import java.util.Currency;
import java.util.Optional;

public class JsonOrderItem implements OrderItem {
    private final JsonNode node;

    public JsonOrderItem(JsonNode node) {
        this.node = node;
    }

    @Override
    public Money price() {
        return new Money.Simple(
                node.get("price").get("value").decimalValue(),
                Currency.getInstance(node.get("price").get("currency").asText())
        );
    }

    @Override
    public Integer quantity() {
        return Optional.ofNullable(node.get("quantity")).map(JsonNode::asInt).orElse(1);
    }

    @Override
    public Optional<String> sku() {
        return Optional.ofNullable(node.get("sku")).map(JsonNode::asText);
    }

    @Override
    public String description() {
        return node.get("description").asText();
    }

    @Override
    public Optional<Money> tax() {
        return Optional.ofNullable(node.get("tax")).map(tn -> new Money.Simple(
                tn.get("value").decimalValue(),
                price().currency()
        ));
    }
}
