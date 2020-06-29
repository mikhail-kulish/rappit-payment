package io.rappit.services.payment.jdbc;

import io.rappit.common.media.Media;
import io.rappit.common.world.finance.Money;
import io.rappit.services.payment.api.Order;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.Currency;
import java.util.Optional;

public class JdbcOrder implements Order {
    private final JdbcTemplate jdbcTemplate;
    private final Long id;

    public JdbcOrder(JdbcTemplate jdbcTemplate, Long id) {
        this.jdbcTemplate = jdbcTemplate;
        this.id = id;
    }

    @Override
    public Long id() {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM `order` WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> rs.getLong("id")
        );
    }

    @Override
    public String reference() {
        return jdbcTemplate.queryForObject(
                "SELECT reference FROM `order` WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> rs.getString("reference")
        );
    }

    @Override
    public Long merchant() {
        return
                jdbcTemplate.queryForObject(
                        "SELECT merchant_id FROM `order` WHERE id = ?",
                        new Object[]{id},
                        (rs, rowNum) -> rs.getLong("MERCHANT_ID")
                );
    }

    @Override
    public Money amount() {
        return jdbcTemplate.queryForObject(
                "SELECT amount, currency FROM `order` WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> new Money.Simple(
                        rs.getBigDecimal("amount"),
                        Currency.getInstance(rs.getString("currency"))
                )
        );
    }

    @Override
    public String description() {
        return jdbcTemplate.queryForObject(
                "SELECT description FROM `order` WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> rs.getString("DESCRIPTION")
        );
    }

    @Override
    public Type type() {
        return jdbcTemplate.queryForObject(
                "SELECT type FROM `order` WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> Type.valueOf(rs.getString("type"))
        );
    }

    @Override
    public Optional<Recurring> recurring() {
        return jdbcTemplate.queryForObject(
                "SELECT recurring_frequency, recurring_amount, recurring_currency, recurring_count, recurring_start FROM `order` WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> rs.getString("recurring_frequency") != null ? Optional.of(new ResultSetOrder.ResultSetRecurring(rs, "")) : Optional.empty()
        );
    }

    public Instant created() {
        return jdbcTemplate.queryForObject(
                "SELECT created FROM `order` WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> rs.getTimestamp("created").toInstant()
        );
    }

    @Override
    public void print(Media media) {
        Order.super.print(media.with("created", created().toEpochMilli()));
    }

}
