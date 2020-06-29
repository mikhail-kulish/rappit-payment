package io.rappit.services.payment.command;

import io.rappit.common.world.location.Location;
import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.api.PaymentForm;
import io.rappit.services.payment.jdbc.JdbcPayment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.function.Function;

public class JdbcInsertPayment implements Function<PaymentForm, Payment> {
    private static final Logger log = LoggerFactory.getLogger(JdbcInsertPayment.class);
    private final JdbcTemplate jdbcTemplate;

    public JdbcInsertPayment(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Payment apply(PaymentForm paymentForm) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("payment")
                .usingGeneratedKeyColumns("id");
        return new JdbcPayment(
                jdbcTemplate,
                insert
                        .executeAndReturnKey(
                                new MapSqlParameterSource()
                                        .addValue("created", new Timestamp(Instant.now().toEpochMilli()))
                                        .addValue("customer_id", paymentForm.customer())
                                        .addValue("payer_email", paymentForm.payer().email())
                                        .addValue("payer_name_first", paymentForm.payer().name().first())
                                        .addValue("payer_name_last", paymentForm.payer().name().last())
                                        .addValue("payer_address", paymentForm.payer().billing().map(Location::address).orElse(null))
                                        .addValue("payer_address_additional", paymentForm.payer().billing().flatMap(Location::additional).orElse(null))
                                        .addValue("payer_address_city", paymentForm.payer().billing().map(Location::city).orElse(null))
                                        .addValue("payer_address_state", paymentForm.payer().billing().flatMap(Location::state).orElse(null))
                                        .addValue("payer_address_postal", paymentForm.payer().billing().map(Location::postal).orElse(null))
                                        .addValue("payer_address_country", paymentForm.payer().billing().map(Location::country).orElse(null))
                                        .addValue("order_id", paymentForm.order().id())
                                        .addValue("status", Payment.Status.PENDING.name())
                        ).longValue()
        );
    }
}
