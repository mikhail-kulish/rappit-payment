package io.rappit.services.payment.command;

import io.rappit.common.world.finance.Money;
import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;

public class JdbcInsertOrderItems implements BiFunction<Order, Collection<OrderItem>, Order> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcInsertOrderItems(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Order apply(Order order, Collection<OrderItem> items) {
        Collection<MapSqlParameterSource> itemsMap = new ArrayList<>(items.size());
        items.forEach(item -> itemsMap.add(
                new MapSqlParameterSource()
                        .addValue("order_id", order.id())
                        .addValue("item_price", item.price().value())
                        .addValue("item_currency", item.price().currency())
                        .addValue("item_quantity", item.quantity())
                        .addValue("item_sku", item.sku().orElse(null))
                        .addValue("item_description", item.description())
                        .addValue("item_tax", item.tax().map(Money::value).orElse(null))
                )
        );
        new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("order_item")
                .executeBatch(itemsMap.toArray(new SqlParameterSource[]{}));
        return order;
    }

}
