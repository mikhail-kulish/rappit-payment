package io.rappit.services.payment.jdbc;

import io.rappit.services.payment.PaymentsList;
import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.api.PaymentForm;
import io.rappit.services.payment.api.filter.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Iterator;

public class JdbcAdminPayments implements PaymentsList {
    private final JdbcTemplate jdbcTemplate;
    private final Pageable pageable;

    public JdbcAdminPayments(JdbcTemplate jdbcTemplate, Pageable pageable) {
        this.jdbcTemplate = jdbcTemplate;
        this.pageable = pageable;
    }

    @Override
    public Payment create(PaymentForm paymentForm) {
        return null;
    }

    @Override
    public Iterator iterator() {
        return jdbcTemplate.query(
                "SELECT id FROM payment LIMIT ? OFFSET ?",
                new Object[]{pageable.size(), pageable.number() * pageable.size()},
                (rs, rowNum) -> new JdbcPayment(jdbcTemplate, rs.getLong("id"))
        ).iterator();
    }
}
