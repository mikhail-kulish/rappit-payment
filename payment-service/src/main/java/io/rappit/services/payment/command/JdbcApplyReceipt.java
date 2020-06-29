package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Payment;
import io.rappit.services.processor.api.Receipt;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Function;

public class JdbcApplyReceipt implements Function<Receipt, Payment> {
    private final TransactionTemplate transactionTemplate;
    private final JdbcInsertReceipt insertReceipt;
    private final JdbcUpdatePaymentStatus updatePaymentStatus;
    private final PublishPaymentEvent publishPaymentEvent;

    public JdbcApplyReceipt(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, ApplicationEventPublisher eventPublisher) {
        this.transactionTemplate = transactionTemplate;
        this.insertReceipt = new JdbcInsertReceipt(jdbcTemplate);
        this.updatePaymentStatus = new JdbcUpdatePaymentStatus(jdbcTemplate);
        this.publishPaymentEvent = new PublishPaymentEvent(eventPublisher);
    }

    @Override
    public Payment apply(Receipt receipt) {
        return transactionTemplate.execute(
                status -> insertReceipt
                        .andThen(updatePaymentStatus)
                        .andThen(publishPaymentEvent)
                        .apply(receipt)
        );
    }

}
