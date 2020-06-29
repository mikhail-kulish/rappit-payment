package io.rappit.services.payment.jdbc;

import io.rappit.common.world.location.Location;
import io.rappit.common.world.people.Name;
import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.Payer;
import io.rappit.services.payment.api.Payment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

public class JdbcPayment implements Payment {
    private final JdbcTemplate jdbcTemplate;
    private final Long id;

    public JdbcPayment(JdbcTemplate jdbcTemplate, Long id) {
        this.jdbcTemplate = jdbcTemplate;
        this.id = id;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public String customer() {
        return
                jdbcTemplate.queryForObject(
                        "SELECT CUSTOMER_ID FROM payment WHERE id = ?",
                        new Object[]{id},
                        (rs, rowNum) -> rs.getString("CUSTOMER_ID")
                );
    }

    @Override
    public Payer payer() {
        return jdbcTemplate.queryForObject(
                "SELECT payer_email, payer_name_first, payer_name_last, payer_address, payer_address_additional, payer_address_city, payer_address_state, payer_address_postal, payer_address_country FROM payment WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> new Payer.Simple(
                        rs.getString("payer_email"),
                        new Name.Simple(
                                rs.getString("payer_name_first"),
                                rs.getString("payer_name_last")
                        ),
                        Optional.ofNullable(
                                rs.getString("payer_address")
                        ).map(
                                address -> {
                                    try {
                                        return new Location.Simple(
                                                address,
                                                rs.getString("payer_address_city"),
                                                rs.getString("payer_address_postal"),
                                                rs.getString("payer_address_country"),
                                                rs.getString("payer_address_additional"),
                                                rs.getString("payer_address_state")
                                        );
                                    } catch (SQLException e) {
                                        return null;
                                    }
                                }
                        ).orElse(null)
                )
        );
    }

    @Override
    public Order order() {
        return jdbcTemplate.queryForObject(
                "SELECT order_id FROM payment WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> new JdbcOrder(jdbcTemplate, rs.getLong("order_id"))
        );
    }

    @Override
    public Status status() {
        return jdbcTemplate.queryForObject(
                "SELECT STATUS FROM payment WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> Status.valueOf(rs.getString("STATUS"))
        );
    }

    @Override
    public Instant created() {
        return jdbcTemplate.queryForObject(
                "SELECT CREATED FROM payment WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> rs.getTimestamp("CREATED").toInstant()
        );
    }

    @Override
    public Optional<Instant> completed() {
        return jdbcTemplate.queryForObject(
                "SELECT COMPLETED FROM payment WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> Optional.ofNullable(rs.getTimestamp("COMPLETED")).map(Timestamp::toInstant)
        );
    }

}
