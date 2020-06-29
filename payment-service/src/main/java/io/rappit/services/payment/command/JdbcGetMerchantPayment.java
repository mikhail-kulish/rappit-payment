package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.jdbc.JdbcPayment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JdbcGetMerchantPayment implements BiFunction<Long, Long, Payment> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGetMerchantPayment(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Payment apply(Long merchantId, Long paymentId) {
        return jdbcTemplate.queryForObject(
                "SELECT p.id as id FROM payment p LEFT JOIN `order` o on o.id = p.order_id WHERE o.merchant_id=? AND p.id=?",
                new Object[]{merchantId, paymentId},
                (rs, rowNum) -> new JdbcPayment(jdbcTemplate, rs.getLong("id"))
        );
    }

}
