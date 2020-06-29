package io.rappit.services.payment.controller.v1;

import io.rappit.services.payment.api.filter.Pageable;
import io.rappit.services.payment.command.JdbcGetCustomerPayments;
import io.rappit.services.payment.command.JdbcGetPayment;
import io.rappit.services.payment.command.JdbcGetPayments;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController("internalPaymentController")
@RequestMapping("/v1/payment")
@CrossOrigin(origins = {"https://rappitpay.com", "http://localhost:3000"}, allowCredentials = "true")
public class PaymentController {
    private final JdbcGetCustomerPayments getCustomerPayments;
    private final JdbcGetPayments getPayments;
    private final JdbcGetPayment getPayment;

    public PaymentController(JdbcTemplate jdbcTemplate) {
        this.getCustomerPayments = new JdbcGetCustomerPayments(jdbcTemplate);
        this.getPayments = new JdbcGetPayments(jdbcTemplate);
        this.getPayment = new JdbcGetPayment(jdbcTemplate);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity payments(@RequestParam(name = "customer_id", required = false) String customerId,
                                   @RequestParam(name = "merchant_id", required = false) Long merchantId,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                   @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(
                Optional.ofNullable(customerId)
                        .map(cstId -> getCustomerPayments.apply(cstId, new Pageable.Simple(page, size)))
                        .orElseGet(() -> getPayments.apply(new Pageable.Simple(page, size)))
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity payment(@PathVariable("id") Long id) {
        return ResponseEntity.ok(getPayment.apply(id));
    }

    @GetMapping("/{id}/receipt")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity receipt(@PathVariable("id") Long id) {
        return ResponseEntity.ok(getPayment.apply(id));
    }

}
