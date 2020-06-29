package io.rappit.services.payment;

import io.rappit.common.media.Printable;
import io.rappit.services.payment.api.Payer;
import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.api.PaymentForm;
import io.rappit.services.payment.command.JdbcCreateOrder;
import io.rappit.services.payment.jdbc.JdbcOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = {"https://rappitpay.com", "https://rappit.io"}, allowCredentials = "true")
@RestController
@RequestMapping("/payment/customer")
public class CustomerPaymentController {
    private final JdbcTemplate jdbcTemplate;
    private final Payments payments;

    public CustomerPaymentController(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, Payments payments) {
        this.jdbcTemplate = jdbcTemplate;
        this.payments = payments;
    }

    @PostMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity create(@PathVariable("orderId") Long orderId, @RequestBody Payer payer, @AuthenticationPrincipal String customerId) throws PaymentException {
        Payment payment = payments.create(
                new PaymentForm.Simple(customerId, new JdbcOrder(jdbcTemplate, orderId), payer)
        );
        return ResponseEntity.ok(
                payment
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity get(@PathVariable("id") Long id, @AuthenticationPrincipal String customerId) {
        return ResponseEntity.ok(
                (Printable) media -> {
                    media.with("redirect", "https://rappit.io/demo.html?status=approved");
                    payments.get(id, customerId).print(media);
                }
        );
    }
}
