package io.rappit.services.payment.jdbc;

import io.rappit.services.payment.Payments;
import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.api.PaymentForm;
import io.rappit.services.payment.command.JdbcInsertPayment;
import io.rappit.services.processor.api.Receipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;

public class JdbcPayments implements Payments {
    private static final Logger log = LoggerFactory.getLogger(JdbcPayments.class);
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final JdbcInsertPayment insertPayment;

    public JdbcPayments(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
        this.insertPayment = new JdbcInsertPayment(jdbcTemplate);
    }

    @Override
    public Payment create(PaymentForm paymentForm) {
        return transactionTemplate.execute(
                status -> insertPayment.apply(paymentForm)
        );
    }

    @Override
    public Payment get(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM payment WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) -> new JdbcPayment(jdbcTemplate, rs.getLong("id"))
        );
    }

    @Override
    public Payment get(Long id, String customer) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM payment WHERE id = ? AND CUSTOMER_ID = ?",
                new Object[]{id, customer},
                (rs, rowNum) -> new JdbcPayment(jdbcTemplate, rs.getLong("id"))
        );
    }

    @Override
    public Payment apply(Receipt receipt) {
        final Long id = Long.valueOf(receipt.reference());
        jdbcTemplate.update(
                "UPDATE payment SET STATUS = ?, COMPLETED = CURRENT_TIMESTAMP() WHERE ID = ?",
                status(receipt).name(),
                id
        );
        return new JdbcPayment(jdbcTemplate, id);
    }

    @Override
    public Collection<Payment> find(String customer) {
        return jdbcTemplate.query(
                "SELECT id FROM payment WHERE CUSTOMER_ID = ?",
                new Object[]{customer},
                (rs, rowNum) -> new JdbcPayment(jdbcTemplate, rs.getLong("id"))
        );
    }

    private static Payment.Status status(Receipt receipt) {
        switch (receipt.status()){
            case APPROVED:
                return Payment.Status.APPROVED;
            case DECLINED:
                return Payment.Status.DECLINED;
            default:
                return Payment.Status.ERROR;
        }
    }


}
