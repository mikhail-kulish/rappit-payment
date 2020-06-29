package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.jdbc.JdbcPayment;
import io.rappit.services.processor.api.Receipt;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.function.Function;

public class JdbcUpdatePaymentStatus implements Function<Receipt, Payment> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcUpdatePaymentStatus(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static Payment.Status status(Receipt receipt) {
        switch (receipt.status()) {
            case APPROVED:
                return Payment.Status.APPROVED;
            case DECLINED:
                return Payment.Status.DECLINED;
            default:
                return Payment.Status.ERROR;
        }
    }

    @Override
    public Payment apply(Receipt receipt) {
        final Long id = jdbcTemplate.queryForObject("SELECT payment_id FROM payment_transaction WHERE id=?", Long.class, receipt.reference());
        jdbcTemplate.update(
                "UPDATE payment SET STATUS = ?, COMPLETED = CURRENT_TIMESTAMP() WHERE ID = ?",
                status(receipt).name(),
                id
        );
        return new JdbcPayment(jdbcTemplate, id);
    }


}
