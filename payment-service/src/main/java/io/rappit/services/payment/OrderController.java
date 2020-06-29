package io.rappit.services.payment;

import io.rappit.services.payment.api.OrderForm;
import io.rappit.services.payment.api.filter.Pageable;
import io.rappit.services.payment.command.*;
import io.rappit.services.payment.jdbc.JdbcOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = {"https://rappitpay.com", "http://localhost:3000"}, allowCredentials = "true")
public class OrderController {
    private final JdbcTemplate jdbcTemplate;
    private final JdbcCreateOrder createOrder;
    private final JdbcGetOrder getOrder;
    private final JdbcGetOrderItems getOrderItems;
    private final JdbcGetOrderPayments getOrderPayments;
    private final JdbcGetOrderPayer getOrderPayer;

    public OrderController(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.createOrder = new JdbcCreateOrder(jdbcTemplate, transactionTemplate);
        this.getOrder = new JdbcGetOrder(jdbcTemplate);
        this.getOrderItems = new JdbcGetOrderItems(jdbcTemplate);
        this.getOrderPayments = new JdbcGetOrderPayments(jdbcTemplate);
        this.getOrderPayer = new JdbcGetOrderPayer(jdbcTemplate);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody OrderForm orderForm) {
        return ResponseEntity.ok(createOrder.apply(orderForm));
    }

    @GetMapping("{id}")
    public ResponseEntity get(@PathVariable Long id) {
        return ResponseEntity.ok(new JdbcOrder(jdbcTemplate, id));
    }

    @GetMapping("{id}/items")
    public ResponseEntity getItems(@PathVariable Long id) {
        return ResponseEntity.ok(getOrder.andThen(getOrderItems).apply(id));
    }

    @GetMapping("{id}/payer")
    public ResponseEntity getPayer(@PathVariable Long id) {
        return ResponseEntity.ok(getOrder.andThen(getOrderPayer).apply(id));
    }

    @GetMapping("{id}/payments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getOrderPayments(@PathVariable Long id) {
        return ResponseEntity.ok(
                getOrder.andThen(
                        order -> getOrderPayments.apply(order, new Pageable.Simple(0, 200))
                ).apply(id).content()
        );
    }

}
