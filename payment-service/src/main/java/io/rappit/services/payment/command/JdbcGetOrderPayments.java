package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.api.filter.Page;
import io.rappit.services.payment.api.filter.Pageable;
import io.rappit.services.payment.jdbc.ResultSetPayment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JdbcGetOrderPayments implements BiFunction<Order, Pageable, Page<Payment>> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGetOrderPayments(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Page<Payment> apply(Order order, Pageable pageable) {
        final Long total = jdbcTemplate.queryForObject(
                "SELECT count(id) FROM payment WHERE order_id = ?",
                new Object[]{order.id()},
                Long.class
        );
        Collection<Payment> payments = jdbcTemplate.query(
                "SELECT * FROM payment WHERE order_id = ? ORDER BY id DESC LIMIT ?, ?",
                new Object[]{order.id(), pageable.number() * pageable.size(), pageable.size()},
                (rs, rowNum) -> new ResultSetPayment(rs, order)
        );
        return new Page.Simple<>(total, pageable.number(), payments);
    }
}
