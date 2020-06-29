package io.rappit.services.payment.jdbc;

import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.Payer;
import io.rappit.services.payment.api.Payment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

public class ResultSetPayment implements Payment {
    private final Long id;
    private final Instant created;
    private final Instant completed;
    private final String customer;
    private final Status status;
    private final Payer payer;
    private final Order order;

    public ResultSetPayment(ResultSet resultSet, Order order) throws SQLException {
        this(resultSet, "", order);
    }

    public ResultSetPayment(ResultSet resultSet, String prefix, Order order) throws SQLException {
        this.id = resultSet.getLong(prefix.concat("id"));
        this.created = resultSet.getTimestamp(prefix.concat("created")).toInstant();
        this.completed = Optional.ofNullable(resultSet.getTimestamp(prefix.concat("completed"))).map(Timestamp::toInstant).orElse(null);
        this.customer = resultSet.getString(prefix.concat("customer_id"));
        this.status = Status.valueOf(resultSet.getString(prefix.concat("status")));
        this.payer = new ResultSetPayer(resultSet, prefix);
        this.order = order;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public Instant created() {
        return created;
    }

    @Override
    public Optional<Instant> completed() {
        return Optional.ofNullable(completed);
    }

    @Override
    public String customer() {
        return customer;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public Payer payer() {
        return payer;
    }

    @Override
    public Order order() {
        return order;
    }
}
