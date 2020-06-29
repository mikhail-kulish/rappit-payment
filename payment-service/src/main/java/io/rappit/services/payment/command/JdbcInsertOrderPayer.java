package io.rappit.services.payment.command;

import io.rappit.common.world.location.Location;
import io.rappit.common.world.people.Name;
import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.Payer;
import io.rappit.services.payment.jdbc.JdbcOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.function.BiFunction;

public class JdbcInsertOrderPayer implements BiFunction<Order, Payer, Order> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcInsertOrderPayer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Order apply(Order order, Payer payer) {
        new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("order_payer")
                .execute(
                        new MapSqlParameterSource()
                                .addValue("order_id", order.id())
                                .addValue("payer_email", payer.email())
                                .addValue("payer_name_first", payer.name().first())
                                .addValue("payer_name_last", payer.name().last())
                                .addValue("payer_address", payer.billing().map(Location::address).orElse(null))
                                .addValue("payer_address_additional", payer.billing().flatMap(Location::additional).orElse(null))
                                .addValue("payer_address_city", payer.billing().map(Location::city).orElse(null))
                                .addValue("payer_address_state", payer.billing().flatMap(Location::state).orElse(null))
                                .addValue("payer_address_postal", payer.billing().map(Location::postal).orElse(null))
                                .addValue("payer_address_country", payer.billing().map(Location::country).orElse(null))
                );
        return order;
    }
}
