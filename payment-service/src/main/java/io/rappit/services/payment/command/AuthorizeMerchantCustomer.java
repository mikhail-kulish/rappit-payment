package io.rappit.services.payment.command;

import io.rappit.services.payment.jdbc.ResultSetOrder;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AuthorizeMerchantCustomer implements BiFunction<Long, String, Long> {
    private final JdbcTemplate jdbcTemplate;

    public AuthorizeMerchantCustomer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long apply(Long merchantId, String customerId) {
        return jdbcTemplate.queryForObject(
                "SELECT merchant_id FROM rappit.merchant_customer WHERE merchant_id = ? AND customer_id = ?",
                new Object[]{merchantId, customerId},
                (rs, rowNum) -> rs.getLong("merchant_id")
        );
    }
}
