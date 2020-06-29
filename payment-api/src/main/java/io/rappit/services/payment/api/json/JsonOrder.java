package io.rappit.services.payment.api.json;

import com.fasterxml.jackson.databind.JsonNode;
import io.rappit.common.world.finance.Money;
import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.OrderItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JsonOrder implements Order {
    private final JsonNode node;

    public JsonOrder(JsonNode node) {
        this.node = node;
    }

    @Override
    public Long id() {
        return node.get("id").asLong();
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
    public Money amount() {
        return new Money.Simple(
                node.get("amount").get("value").decimalValue(),
                Currency.getInstance(node.get("amount").get("currency").asText())
        );
    }

    @Override
    public String description() {
        return node.get("description").asText();
    }

    @Override
    public Type type() {
        return Type.valueOf(node.get("type").asText());
    }

    @Override
    public Optional<Recurring> recurring() {
        return Optional.ofNullable(node.get("recurring")).map(JsonRecurring::new);
    }

    static class JsonRecurring implements Recurring {
        private final JsonNode node;

        public JsonRecurring(JsonNode node) {
            this.node = node;
        }

        @Override
        public Frequency frequency() {
            if(isValidEnum(Frequency.class, node.get("frequency"))){
                return Frequency.valueOf(node.get("frequency").asText());
            }
            throw new IllegalArgumentException("Incorrect `frequency` field");
        }

        @Override
        public Money amount() {
            return new Money.Simple(
                    node.get("amount").get("value").decimalValue(),
                    Currency.getInstance(node.get("amount").get("currency").asText())
            );
        }

        @Override
        public Integer count() {
            return node.get("count").asInt();
        }

        @Override
        public Optional<LocalDate> start() {
            return Optional.ofNullable(node.get("start")).map(JsonNode::asText).map(start -> LocalDate.parse(start, DateTimeFormatter.BASIC_ISO_DATE));
        }
    }
    private static boolean isValidEnum(Class<? extends Enum> enumType, JsonNode node) {
        return Arrays.stream(enumType.getEnumConstants()).map(Enum::name).anyMatch(type -> type.equals(node.asText().toUpperCase()));
    }

}
