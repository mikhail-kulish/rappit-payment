package io.rappit.services.payment.command;

import io.rappit.services.payment.PaymentTransaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.function.Function;

public class JdbcInsertPaymentTransaction implements Function<PaymentTransaction, PaymentTransaction> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcInsertPaymentTransaction(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PaymentTransaction apply(PaymentTransaction paymentTransaction) {
        new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("payment_transaction")
                .execute(
                        new MapSqlParameterSource()
                                .addValue("id", paymentTransaction.id())
                                .addValue("created", Timestamp.from(Instant.now()))
                                .addValue("payment_id", paymentTransaction.payment().id())
                                .addValue("account_info", paymentTransaction.account().info())
                                .addValue("account_id", paymentTransaction.account().id().orElse(null))
                                .addValue("type", paymentTransaction.type().name())
                );
        return paymentTransaction;
    }
}
