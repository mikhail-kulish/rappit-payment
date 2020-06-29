package io.rappit.services.payment.command;

import io.rappit.common.world.finance.Money;
import io.rappit.services.payment.OrderFormTotal;
import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.OrderForm;
import io.rappit.services.payment.jdbc.JdbcOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.Date;
import java.util.function.Function;

public class JdbcInsertOrder implements Function<OrderForm, Order> {
    private final JdbcTemplate jdbcTemplate;

    public JdbcInsertOrder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Order apply(OrderForm orderForm) {
        final Money total = new OrderFormTotal(orderForm);
        final MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("merchant_id", orderForm.merchant())
                .addValue("reference", orderForm.reference())
                .addValue("description", orderForm.description())
                .addValue("type", orderForm.type())
                .addValue("amount", total.value())
                .addValue("currency", total.currency().getCurrencyCode());
        orderForm.recurring().ifPresent(
                recurring -> parameterSource.addValue("recurring_frequency", recurring.frequency().name())
                        .addValue("recurring_amount", recurring.amount().value())
                        .addValue("recurring_currency", recurring.amount().currency().getCurrencyCode())
        );
        orderForm.recurring().map(Order.Recurring::count).ifPresent(
                count -> parameterSource.addValue("recurring_count", count)
        );
        orderForm.recurring().flatMap(Order.Recurring::start).ifPresent(
                start -> parameterSource.addValue("recurring_start", Date.valueOf(start))
        );
        final Long orderId = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("`order`")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(parameterSource)
                .longValue();
        return new JdbcOrder(
                jdbcTemplate,
                orderId
        );
    }

}
