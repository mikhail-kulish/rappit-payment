package io.rappit.services.payment.command.statistics;

import io.rappit.common.media.Printable;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.function.Function;

public class JdbcMerchantPaymentStat implements Function<Long, Printable> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcMerchantPaymentStat(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Printable apply(Long merchantId) {
        return jdbcTemplate.queryForObject(
                "select count(id) as total, " +
                        "round(avg(amount), 2) as average_amount " +
                        "from payment where merchant_id = ?",
                new Object[]{merchantId},
                (rs, rowNum) -> {
                    final Long orders = rs.getLong("total");
                    final BigDecimal averageAmount  = rs.getBigDecimal("average_amount");
                    return media -> media.with("total", orders)
                            .with("average_amount", averageAmount);
                }
        );
    }
}
