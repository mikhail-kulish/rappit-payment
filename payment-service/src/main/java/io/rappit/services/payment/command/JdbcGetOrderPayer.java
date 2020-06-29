package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.Payer;
import io.rappit.services.payment.jdbc.ResultSetPayer;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.function.Function;

public class JdbcGetOrderPayer implements Function<Order, Payer> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGetOrderPayer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Payer apply(Order order) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM order_payer WHERE order_id = ?",
                new Object[]{order.id()},
                (rs, rowNum) -> new ResultSetPayer(rs)
        );
    }

}
