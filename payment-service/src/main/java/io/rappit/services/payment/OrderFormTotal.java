package io.rappit.services.payment;

import io.rappit.common.world.finance.Money;
import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.OrderForm;

import java.math.BigDecimal;
import java.util.Currency;

public class OrderFormTotal implements Money {
    private final Money total;

    public OrderFormTotal(OrderForm orderForm) {
        if (orderForm.type() == Order.Type.OPEN || orderForm.type() == Order.Type.SINGLE) {
            this.total = new Simple(
                    orderForm.items().stream().map(item -> item.price().value()
                            .add(item.tax().map(Money::value).orElse(BigDecimal.ZERO))
                            .multiply(new BigDecimal(item.quantity()))
                    ).reduce(BigDecimal.ZERO, BigDecimal::add),
                    orderForm.items().stream().map(item -> item.price().currency())
                            .findFirst().orElse(Currency.getInstance("USD"))
            );
        } else if (orderForm.type() == Order.Type.INSTALLMENT) {
            Order.Recurring recurring = orderForm.recurring().get();
            this.total = new Simple(recurring.amount().value().multiply(new BigDecimal(recurring.count())), recurring.amount().currency());
        } else { // RECURRING
            Order.Recurring recurring = orderForm.recurring().get();
            this.total = recurring.amount();
        }
    }

    @Override
    public BigDecimal value() {
        return total.value();
    }

    @Override
    public Currency currency() {
        return total.currency();
    }
}
