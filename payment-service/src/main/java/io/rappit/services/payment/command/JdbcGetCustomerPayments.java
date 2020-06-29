package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.api.filter.Page;
import io.rappit.services.payment.api.filter.Pageable;
import io.rappit.services.payment.jdbc.ResultSetOrder;
import io.rappit.services.payment.jdbc.ResultSetPayment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.function.BiFunction;

public class JdbcGetCustomerPayments implements BiFunction<String, Pageable, Page<Payment>> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGetCustomerPayments(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Page<Payment> apply(String customerId, Pageable pageable) {
        final Long total = jdbcTemplate.queryForObject(
                "SELECT count(p.id) FROM payment p LEFT JOIN `order` o ON o.id = p.order_id " +
                        "WHERE p.customer_id = ?",
                new Object[]{customerId},
                Long.class
        );
        final Collection<Payment> payments = jdbcTemplate.query(
                "SELECT p.id as pmnt_id, " +
                        "p.created as pmnt_created, " +
                        "p.completed as pmnt_completed, " +
                        "p.customer_id as pmnt_customer_id, " +
                        "p.payer_email as pmnt_payer_email, " +
                        "p.payer_name_first as pmnt_payer_name_first, " +
                        "p.payer_name_last as pmnt_payer_name_last, " +
                        "p.payer_address as pmnt_payer_address, " +
                        "p.payer_address_additional as pmnt_payer_address_additional, " +
                        "p.payer_address_city as pmnt_payer_address_city, " +
                        "p.payer_address_state as pmnt_payer_address_state, " +
                        "p.payer_address_postal as pmnt_payer_address_postal, " +
                        "p.payer_address_country as pmnt_payer_address_country, " +
                        "p.order_id as pmnt_order_id, " +
                        "p.status as pmnt_status, " +
                        "o.id as ordr_id, " +
                        "o.merchant_id as ordr_merchant_id, " +
                        "o.created as ordr_created, " +
                        "o.amount as ordr_amount, " +
                        "o.currency as ordr_currency, " +
                        "o.reference as ordr_reference, " +
                        "o.description as ordr_description, " +
                        "o.type as ordr_type, " +
                        "o.recurring_frequency as ordr_recurring_frequency, " +
                        "o.recurring_amount as ordr_recurring_amount, " +
                        "o.recurring_currency as ordr_recurring_currency, " +
                        "o.recurring_count as ordr_recurring_count, " +
                        "o.recurring_start as ordr_recurring_start " +
                        "FROM payment p LEFT JOIN `order` o ON o.id = p.order_id WHERE p.customer_id = ? ORDER BY p.id DESC LIMIT ?, ?",
                new Object[]{customerId, pageable.number() * pageable.size(), pageable.size()},
                (rs, rowNum) -> new ResultSetPayment(rs, "pmnt_", new ResultSetOrder(rs, "ordr_"))
        );
        return new Page.Simple<>(total, pageable.number(), payments);
    }
}
