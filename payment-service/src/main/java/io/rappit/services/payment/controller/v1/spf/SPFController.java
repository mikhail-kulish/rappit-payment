package io.rappit.services.payment.controller.v1.spf;

import io.rappit.services.account.api.CardAccount;
import io.rappit.services.merchant.api.Merchant;
import io.rappit.services.payment.CardPaymentTransaction;
import io.rappit.services.payment.api.OrderForm;
import io.rappit.services.payment.api.Payer;
import io.rappit.services.payment.api.PaymentForm;
import io.rappit.services.payment.command.*;
import io.rappit.services.payment.jdbc.JdbcOrder;
import io.rappit.services.payment.jdbc.JdbcPayments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/v1/spf")
@CrossOrigin(origins = {"https://rappitpay.com", "http://localhost:3000"}, allowCredentials = "true")
public class SPFController {
    private final static Logger log = LoggerFactory.getLogger(SPFController.class);
    private final JdbcPayments payments;
    private final JdbcCreateOrder createOrder;
    private final JdbcInsertPayment insertPayment;
    private final JdbcInsertPaymentTransaction insertPaymentTransaction;
    private final JdbcGetPayment getPayment;
    private final ValidateOrderForm validateOrderForm = new ValidateOrderForm();
    private final ValidatePaymentForm validatePaymentForm = new ValidatePaymentForm();
    private final JdbcTemplate jdbcTemplate;
    private final RestProcessTransaction restProcessTransaction;
    private final ValidateCardPaymentTransaction validateCardPaymentTransaction = new ValidateCardPaymentTransaction();

    public SPFController(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, RestTemplate restTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.payments = new JdbcPayments(jdbcTemplate, transactionTemplate);
        this.createOrder = new JdbcCreateOrder(jdbcTemplate, transactionTemplate);
        this.insertPayment = new JdbcInsertPayment(jdbcTemplate);
        this.insertPaymentTransaction = new JdbcInsertPaymentTransaction(jdbcTemplate);
        this.restProcessTransaction = new RestProcessTransaction(restTemplate);
        this.getPayment = new JdbcGetPayment(jdbcTemplate);
    }

    @PostMapping("/order")
    @PreAuthorize("hasAuthority('MERCHANT') and #merchant.id().equals(#orderForm.merchant())")
    public ResponseEntity createOrder(@RequestBody OrderForm orderForm, @AuthenticationPrincipal Merchant merchant) {
        return ResponseEntity.ok(
                validateOrderForm.andThen(createOrder).apply(orderForm)
        );
    }

    @PostMapping("/order/{orderId}/payment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity createPayment(@PathVariable("orderId") Long orderId, @RequestBody Payer payer, @AuthenticationPrincipal String customerId) {
        return ResponseEntity.ok(
                validatePaymentForm.andThen(insertPayment)
                        .apply(
                                new PaymentForm.Simple(customerId, new JdbcOrder(jdbcTemplate, orderId), payer)
                        )
        );
    }

    @GetMapping("/payment/{paymentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getPayment(@PathVariable("paymentId") Long paymentId, @AuthenticationPrincipal String customerId) {
        return getPayment.apply(paymentId)
                .filter(payment -> payment.customer().equals(customerId))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/payment/{paymentId}/process")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity processPayment(@PathVariable("paymentId") Long paymentId, @RequestBody CardAccount account, @AuthenticationPrincipal String customerId, @RequestHeader("x-forwarded-for") String ip) {
        return ResponseEntity.ok(
                validateCardPaymentTransaction
                        .andThen(insertPaymentTransaction)
                        .andThen(restProcessTransaction)
                        .apply(
                                new CardPaymentTransaction(
                                        payments.get(paymentId, customerId),
                                        account,
                                        ip.split(", ")[0]
                                )
                        )
        );
    }
}
