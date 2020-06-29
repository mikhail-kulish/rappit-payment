package io.rappit.services.payment.controller.v1.portal;

import io.rappit.services.payment.api.filter.Pageable;
import io.rappit.services.payment.command.JdbcGetOrder;
import io.rappit.services.payment.command.JdbcGetOrderPayments;
import io.rappit.services.payment.command.JdbcGetOrders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController("portalOrderController")
@RequestMapping("/v1/portal/order")
@CrossOrigin(origins = {"https://rappitpay.com", "http://localhost:3000"}, allowCredentials = "true")
@PreAuthorize("hasAuthority('ADMIN')")
public class OrderController {
    private final JdbcGetOrders getOrders;
    private final JdbcGetOrder getOrder;
    private final JdbcGetOrderPayments getOrderPayments;

    public OrderController(JdbcTemplate jdbcTemplate) {
        this.getOrder = new JdbcGetOrder(jdbcTemplate);
        this.getOrders = new JdbcGetOrders(jdbcTemplate);
        this.getOrderPayments = new JdbcGetOrderPayments(jdbcTemplate);
    }

    @GetMapping
    public ResponseEntity orders(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                 @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(getOrders.apply(new Pageable.Simple(page, size)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity order(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(getOrder.apply(orderId));
    }

    @GetMapping("/{orderId}/payment")
    public ResponseEntity payments(@PathVariable("orderId") Long orderId,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                   @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(
                getOrder.andThen(
                        order -> getOrderPayments.apply(order, new Pageable.Simple(page, size))
                ).apply(orderId)
        );
    }
}
