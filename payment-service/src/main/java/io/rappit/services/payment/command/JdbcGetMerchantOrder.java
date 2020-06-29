package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.jdbc.ResultSetOrder;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JdbcGetMerchantOrder implements BiFunction<Long, Long, Order> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGetMerchantOrder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Order apply(Long merchantId, Long orderId) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM `order` WHERE merchant_id = ? AND id = ?",
                new Object[]{merchantId, orderId},
                (rs, rowNum) -> new ResultSetOrder(rs)
        );
    }
}
