package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.api.filter.Page;
import io.rappit.services.payment.api.filter.Pageable;
import io.rappit.services.payment.jdbc.JdbcOrder;
import io.rappit.services.payment.jdbc.ResultSetPayment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.function.Function;

public class JdbcGetPayments implements Function<Pageable, Page<Payment>> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGetPayments(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Page<Payment> apply(Pageable pageable) {
        final Long total = jdbcTemplate.queryForObject("SELECT count(id) FROM payment", (rs, num) -> rs.getLong(1));
        final Collection<Payment> payments = jdbcTemplate.query(
                "SELECT * FROM payment ORDER BY id DESC LIMIT ?, ?",
                new Object[]{pageable.number() * pageable.size(), pageable.size()},
                (rs, rowNum) -> new ResultSetPayment(rs, new JdbcOrder(jdbcTemplate, rs.getLong("order_id")))
        );
        return new Page.Simple<>(total, pageable.number(), payments);
    }
}
