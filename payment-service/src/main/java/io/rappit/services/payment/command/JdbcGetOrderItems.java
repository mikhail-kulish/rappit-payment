package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.OrderItem;
import io.rappit.services.payment.jdbc.ResultSetOrderItem;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.function.Function;

public class JdbcGetOrderItems implements Function<Order, Collection<OrderItem>> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGetOrderItems(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<OrderItem> apply(Order order) {
        return jdbcTemplate.query(
                "SELECT item_price, item_currency, item_quantity, item_description, item_sku, item_tax FROM order_item WHERE order_id = ?",
                new Object[]{order.id()},
                (rs, rowNum) -> new ResultSetOrderItem(rs)
        );
    }

}
