package io.rappit.services.payment.command;

import io.rappit.services.payment.api.OrderForm;

import io.rappit.services.payment.api.Order;

import java.math.BigDecimal;
import java.util.function.Function;

public class ValidateOrderForm implements Function<OrderForm, OrderForm> {
    @Override
    public OrderForm apply(OrderForm orderForm) {
        if (orderForm.reference() == null || orderForm.reference().isBlank()) {
            throw new IllegalArgumentException("Order reference can't be empty");
        }
        switch (orderForm.type()) {
            case OPEN:
            case SINGLE:
                if(orderForm.amount().isEmpty() && orderForm.items().isEmpty()){
                    throw new IllegalArgumentException("Missing required `items` or `amount` field");
                }
                break;
            case RECURRING:
                orderForm.recurring().orElseThrow(() -> new IllegalArgumentException("Missing required `recurring` field"));
                if(orderForm.recurring().get().amount().value().compareTo(BigDecimal.ZERO) < 1) {
                    throw new IllegalArgumentException("Field `recurring.amount` should be more th");
                }
                break;
            case INSTALLMENT:
                orderForm.recurring().orElseThrow(() -> new IllegalArgumentException("Missing required `recurring` field"));
                orderForm.recurring().map(Order.Recurring::count).orElseThrow(() -> new IllegalArgumentException("Missing required `recurring.count` field"));
                break;
        }
        return orderForm;
    }
}
