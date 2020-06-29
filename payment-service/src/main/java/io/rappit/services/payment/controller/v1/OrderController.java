package io.rappit.services.payment.controller.v1;

import io.rappit.services.payment.command.JdbcGetOrder;
import io.rappit.services.payment.command.JdbcGetOrderItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

// Supposed to be internal
@RestController("internalOrderController")
@RequestMapping("/v1/order")
@CrossOrigin(origins = {"https://rappitpay.com", "http://localhost:3000"}, allowCredentials = "true")
public class OrderController {
    private final static Logger log = LoggerFactory.getLogger(OrderController.class);
    private final JdbcGetOrder getOrder;
    private final JdbcGetOrderItems getOrderItems;

    public OrderController(JdbcTemplate jdbcTemplate) {
        this.getOrder = new JdbcGetOrder(jdbcTemplate);
        this.getOrderItems = new JdbcGetOrderItems(jdbcTemplate);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity getOrder(@PathVariable("orderId") Long id) {
        return ResponseEntity.ok(
                getOrder.apply(id)
        );
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity getOrderItems(@PathVariable("orderId") Long id) {
        return ResponseEntity.ok(
                getOrder.andThen(getOrderItems).apply(id)
        );
    }
}
