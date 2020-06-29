package io.rappit.services.payment.api.json;

import com.fasterxml.jackson.databind.JsonNode;
import io.rappit.common.world.finance.Money;
import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.OrderForm;
import io.rappit.services.payment.api.OrderItem;
import io.rappit.services.payment.api.Payer;
import io.rappit.services.payment.api.json.JsonOrder;
import io.rappit.services.payment.api.json.JsonPayer;

import java.util.*;

public class JsonOrderForm implements OrderForm {
    private final JsonNode node;

    public JsonOrderForm(JsonNode node) {
        this.node = node;
    }

    @Override
    public String reference() {
        return node.get("reference").asText();
    }

    @Override
    public Long merchant() {
        return node.get("merchant").asLong();
    }

    @Override
    public String description() {
        return node.get("description").asText();
    }

    @Override
    public Collection<OrderItem> items() {
        final Collection<OrderItem> result = new ArrayList<>();
        if (node.get("items") != null) {
            final Iterator<JsonNode> elements = node.get("items").elements();
            elements.forEachRemaining(item -> result.add(new JsonItem(item)));
        }
        return result;
    }

    @Override
    public Optional<Payer> payer() {
        return Optional.ofNullable(node.get("payer")).map(JsonPayer::new);
    }

    @Override
    public Optional<Money> amount() {
        return Optional.ofNullable(node.get("amount"))
                .map(amountNode -> new Money.Simple(
                        amountNode.get("value").decimalValue(),
                        Currency.getInstance(amountNode.get("currency").asText().toUpperCase())
                ));
    }

    @Override
    public Order.Type type() {
        if(node.get("type") != null && !isValidEnum(Order.Type.class, node.get("type"))) {
            throw new IllegalArgumentException("Incorrect `type` field");
        }
        return Optional.ofNullable(node.get("type")).map(JsonNode::asText).map(Order.Type::valueOf).orElse(Order.Type.SINGLE);
    }

    @Override
    public Optional<Order.Recurring> recurring() {
        return Optional.ofNullable(node.get("recurring")).map(JsonOrder.JsonRecurring::new);
    }

    private static class JsonItem implements OrderItem {
        private final JsonNode node;

        public JsonItem(JsonNode node) {
            this.node = node;
        }

        @Override
        public Money price() {
            return new Money.Simple(
                    node.get("price").get("value").decimalValue(),
                    Currency.getInstance(node.get("price").get("currency").asText().toUpperCase())
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

    private boolean isValidEnum(Class<? extends Enum> enumType, JsonNode node) {
        return Arrays.stream(enumType.getEnumConstants()).map(Enum::name).anyMatch(type -> type.equals(node.asText().toUpperCase()));
    }
}
