package io.rappit.services.payment.api;

import io.rappit.common.media.Media;
import io.rappit.common.media.Printable;

public interface PaymentForm extends Printable {
    String customer();

    Order order();

    Payer payer();

    @Override
    default void print(Media media) {
        media.with("customer", customer())
                .with("order", order())
                .with("payer", payer());
    }

    class Simple implements PaymentForm {
        private final String customer;
        private final Order order;
        private final Payer payer;

        public Simple(String customer, Order order, Payer payer) {
            this.customer = customer;
            this.order = order;
            this.payer = payer;
        }

        @Override
        public String customer() {
            return customer;
        }

        @Override
        public Order order() {
            return order;
        }

        @Override
        public Payer payer() {
            return payer;
        }
    }
}
