package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.jdbc.JdbcPayment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.function.Function;

public class JdbcGetPayment implements Function<Long, Optional<Payment>> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGetPayment(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Payment> apply(Long paymentId) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM payment WHERE id=?",
                new Object[]{paymentId},
                (rs, rowNum) -> Optional.of(new JdbcPayment(jdbcTemplate, rs.getLong("id")))
        );
    }

}
