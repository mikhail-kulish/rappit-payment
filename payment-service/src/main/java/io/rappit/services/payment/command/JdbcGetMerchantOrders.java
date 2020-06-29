package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.filter.Page;
import io.rappit.services.payment.api.filter.Pageable;
import io.rappit.services.payment.jdbc.ResultSetOrder;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JdbcGetMerchantOrders implements BiFunction<Long, Pageable, Page<Order>> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcGetMerchantOrders(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Page<Order> apply(Long merchantId, Pageable pageable) {
        final Long total = jdbcTemplate.queryForObject(
                "SELECT count(id) FROM `order` WHERE merchant_id = ?",
                new Object[]{merchantId},
                (rs, num) -> rs.getLong(1)
        );
        final Collection<Order> orders = jdbcTemplate.query(
                "SELECT * FROM `order` WHERE merchant_id = ? ORDER BY id DESC LIMIT ?, ?",
                new Object[]{merchantId, pageable.number() * pageable.size(), pageable.size()},
                (rs, rowNum) -> new ResultSetOrder(rs)
        );
        return new Page.Simple<>(total, pageable.number(), orders);
    }
}
