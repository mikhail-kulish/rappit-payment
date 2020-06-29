package io.rappit.services.payment.controller.v1;

import io.rappit.services.merchant.api.Merchant;
import io.rappit.services.payment.api.OrderForm;
import io.rappit.services.payment.api.filter.Pageable;
import io.rappit.services.payment.command.JdbcCreateOrder;
import io.rappit.services.payment.command.JdbcGetMerchantOrders;
import io.rappit.services.payment.command.JdbcGetMerchantPayments;
import io.rappit.services.payment.command.ValidateOrderForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("v1MerchantController")
@RequestMapping("/v1/merchant")
@CrossOrigin(origins = {"https://rappitpay.com", "http://localhost:3000"}, allowCredentials = "true")
public class MerchantController {
    private final static Logger log = LoggerFactory.getLogger(MerchantController.class);
    private final JdbcGetMerchantOrders merchantOrders;
    private final JdbcCreateOrder createOrder;
    private final ValidateOrderForm validateOrderForm = new ValidateOrderForm();
    private final JdbcGetMerchantPayments getMerchantPayments;

    public MerchantController(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.merchantOrders = new JdbcGetMerchantOrders(jdbcTemplate);
        this.createOrder = new JdbcCreateOrder(jdbcTemplate, transactionTemplate);
        this.getMerchantPayments = new JdbcGetMerchantPayments(jdbcTemplate);
    }

    @GetMapping("/order")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity listOrders(@AuthenticationPrincipal Merchant merchant) {
        return ResponseEntity.ok(merchantOrders.apply(merchant.id(), new Pageable.Simple(0, 200)).content());
    }

    @PostMapping("/order")
    @PreAuthorize("isAuthenticated() and #merchant.id().equals(#orderForm.merchant())")
    public ResponseEntity createOrder(@RequestBody OrderForm orderForm, @AuthenticationPrincipal Merchant merchant) {
        return ResponseEntity.ok(
                createOrder.compose(validateOrderForm).apply(orderForm)
        );
    }

    @GetMapping("/{id}/payment")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity payments(@PathVariable("id") Long merchantId,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                   @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(
                getMerchantPayments.apply(merchantId, new Pageable.Simple(page, size)).content()
        );
    }
}
