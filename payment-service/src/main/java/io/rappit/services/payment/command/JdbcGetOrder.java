package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.jdbc.JdbcOrder;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.function.Function;

public class JdbcGetOrder implements Function<Long, Order> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGetOrder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Order apply(Long orderId) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM `order` WHERE id = ?",
                new Object[]{orderId},
                (rs, rowNum) -> new JdbcOrder(jdbcTemplate, rs.getLong("id"))
        );
    }
}
