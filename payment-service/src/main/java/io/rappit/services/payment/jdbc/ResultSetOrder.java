package io.rappit.services.payment.jdbc;

import io.rappit.common.media.Media;
import io.rappit.common.world.finance.Money;
import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.OrderItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.Optional;

public class ResultSetOrder implements Order {
    private final Long id;
    private final Instant created;
    private final Long merchant;
    private final String reference;
    private final Money amount;
    private final String description;
    private final Type type;
    private Recurring recurring;

    public ResultSetOrder(ResultSet resultSet) throws SQLException {
        this(resultSet, "");
    }

    public ResultSetOrder(ResultSet resultSet, String prefix) throws SQLException {
        this.id = resultSet.getLong(prefix.concat("id"));
        this.created = resultSet.getTimestamp(prefix.concat("created")).toInstant();
        this.merchant = resultSet.getLong(prefix.concat("merchant_id"));
        this.reference = resultSet.getString(prefix.concat("reference"));
        this.amount = new Money.Simple(resultSet.getBigDecimal(prefix.concat("amount")), Currency.getInstance(resultSet.getString(prefix.concat("currency"))));
        this.description = resultSet.getString(prefix.concat("description"));
        this.type = Type.valueOf(resultSet.getString(prefix.concat("type")));
        this.recurring = resultSet.getString(prefix.concat("recurring_frequency")) != null ? new ResultSetRecurring(resultSet, prefix) : null;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public Long merchant() {
        return merchant;
    }

    @Override
    public String reference() {
        return reference;
    }

    @Override
    public Money amount() {
        return amount;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public Optional<Recurring> recurring() {
        return Optional.ofNullable(recurring);
    }

    @Override
    public void print(Media media) {
        Order.super.print(media.with("created", created.toEpochMilli()));
    }

    static class ResultSetRecurring implements Recurring {
        private final Frequency frequency;
        private final Money amount;
        private final Integer count;
        private final LocalDate start;

        public ResultSetRecurring(ResultSet resultSet, String prefix) throws SQLException {
            this.frequency = Frequency.valueOf(resultSet.getString(prefix.concat("recurring_frequency")));
            this.amount = new Money.Simple(resultSet.getBigDecimal(prefix.concat("recurring_amount")), Currency.getInstance(resultSet.getString(prefix.concat("recurring_currency"))));
            this.count = resultSet.getInt(prefix.concat("recurring_count"));
            this.start = resultSet.getDate(prefix.concat("recurring_start")) != null ? resultSet.getDate(prefix.concat("recurring_start")).toLocalDate() : null;
        }

        @Override
        public Frequency frequency() {
            return frequency;
        }

        @Override
        public Money amount() {
            return amount;
        }

        @Override
        public Integer count() {
            return count;
        }

        @Override
        public Optional<LocalDate> start() {
            return Optional.ofNullable(start);
        }
    }
}
