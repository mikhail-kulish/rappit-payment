package io.rappit.services.payment.command;

import io.rappit.services.processor.api.Receipt;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.function.Function;

public class JdbcInsertReceipt implements Function<Receipt, Receipt> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcInsertReceipt(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Receipt apply(Receipt receipt) {
        jdbcTemplate.update(
                "INSERT INTO receipt VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                receipt.reference(),
                Timestamp.from(Instant.now()),
                receipt.status().name(),
                receipt.amount().value(),
                receipt.amount().currency().getCurrencyCode(),
                receipt.transaction().orElse(null),
                receipt.message(),
                null,
                receipt.auth().orElse(null)
        );
        return receipt;
    }
}
