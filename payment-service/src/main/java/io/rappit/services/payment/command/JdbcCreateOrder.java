package io.rappit.services.payment.command;

import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.OrderForm;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Function;

public class JdbcCreateOrder implements Function<OrderForm, Order> {
    private final TransactionTemplate transactionTemplate;
    private final JdbcInsertOrder insertOrder;
    private final JdbcInsertOrderItems insertOrderItems;
    private final JdbcInsertOrderPayer insertOrderPayer;

    public JdbcCreateOrder(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
        this.insertOrder = new JdbcInsertOrder(jdbcTemplate);
        this.insertOrderItems = new JdbcInsertOrderItems(jdbcTemplate);
        this.insertOrderPayer = new JdbcInsertOrderPayer(jdbcTemplate);
    }

    @Override
    public Order apply(OrderForm orderForm) {
        return transactionTemplate.execute(
                state -> insertOrder.andThen(
                        order -> insertOrderItems.apply(order, orderForm.items())
                ).andThen(
                        order -> orderForm.payer().map(
                                payer -> insertOrderPayer.apply(order, payer)
                        ).orElse(order)
                ).apply(orderForm)
        );
    }
}
