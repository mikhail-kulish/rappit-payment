package io.rappit.services.payment.controller.v1.portal;

import io.rappit.services.payment.api.filter.Pageable;
import io.rappit.services.payment.command.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController("portalPaymentController")
@RequestMapping("/v1/portal/payment")
@CrossOrigin(origins = {"https://rappitpay.com", "http://localhost:3000"}, allowCredentials = "true")
@PreAuthorize("hasAuthority('ADMIN')")
public class PaymentController {
    private final JdbcGetPayments getPayments;
    private final JdbcGetPayment getPayment;

    public PaymentController(JdbcTemplate jdbcTemplate) {
        this.getPayments = new JdbcGetPayments(jdbcTemplate);
        this.getPayment = new JdbcGetPayment(jdbcTemplate);
    }

    @GetMapping
    public ResponseEntity payments(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                   @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(getPayments.apply(new Pageable.Simple(page, size)));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity payment(@PathVariable("paymentId") Long paymentId) {
        return ResponseEntity.ok(getPayment.apply(paymentId));
    }
}
