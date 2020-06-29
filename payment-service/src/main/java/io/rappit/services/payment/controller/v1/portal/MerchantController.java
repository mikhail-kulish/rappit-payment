package io.rappit.services.payment.controller.v1.portal;

import io.rappit.services.payment.api.OrderForm;
import io.rappit.services.payment.api.filter.Pageable;
import io.rappit.services.payment.command.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/portal/merchant")
@CrossOrigin(origins = {"https://rappitpay.com", "http://localhost:3000"}, allowCredentials = "true")
public class MerchantController {
    private final static Logger log = LoggerFactory.getLogger(MerchantController.class);
    private final JdbcGetMerchantOrders merchantOrders;
    private final JdbcGetMerchantOrder merchantOrder;
    private final JdbcGetOrderItems orderItems;
    private final JdbcGetOrderPayments orderPayments;
    private final JdbcCreateOrder createOrder;
    private final ValidateOrderForm validateOrderForm = new ValidateOrderForm();
    private final JdbcGetMerchantPayments getMerchantPayments;
    private final JdbcGetMerchantPayment getMerchantPayment;

    public MerchantController(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.merchantOrders = new JdbcGetMerchantOrders(jdbcTemplate);
        this.merchantOrder = new JdbcGetMerchantOrder(jdbcTemplate);
        this.orderItems = new JdbcGetOrderItems(jdbcTemplate);
        this.orderPayments = new JdbcGetOrderPayments(jdbcTemplate);
        this.createOrder = new JdbcCreateOrder(jdbcTemplate, transactionTemplate);
        this.getMerchantPayments = new JdbcGetMerchantPayments(jdbcTemplate);
        this.getMerchantPayment = new JdbcGetMerchantPayment(jdbcTemplate);
    }

    @GetMapping("{id}/order")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER')")
    public ResponseEntity listOrders(@PathVariable("id") Long merchantId,
                                     @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                     @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(merchantOrders.apply(merchantId, new Pageable.Simple(page, size)));
    }

    @GetMapping("{id}/order/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER')")
    public ResponseEntity getOrder(@PathVariable("id") Long merchantId, @PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(merchantOrder.apply(merchantId, orderId));
    }

    @GetMapping("{id}/order/{orderId}/items")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER')")
    public ResponseEntity listOrderItems(@PathVariable("id") Long merchantId, @PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(merchantOrder.andThen(orderItems).apply(merchantId, orderId));
    }

    @GetMapping("{id}/order/{orderId}/payment")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER')")
    public ResponseEntity listOrderPayments(@PathVariable("id") Long merchantId,
                                            @PathVariable("orderId") Long orderId,
                                            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(
                merchantOrder.andThen(
                        order -> orderPayments.apply(order, new Pageable.Simple(page, size))
                ).apply(merchantId, orderId));
    }

    @PostMapping("{id}/order")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('CUSTOMER') and #merchantId == #orderForm.merchant())")
    public ResponseEntity createOrder(@PathVariable("id") Long merchantId, @RequestBody OrderForm orderForm) {
        return ResponseEntity.ok(
                createOrder.compose(validateOrderForm).apply(orderForm)
        );
    }

    @GetMapping("/{id}/payment")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER')")
    public ResponseEntity payments(@PathVariable("id") Long merchantId, @AuthenticationPrincipal String customerId,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                   @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(
                getMerchantPayments.apply(merchantId, new Pageable.Simple(page, size))
        );
    }

    @GetMapping("/{id}/payment/{paymentId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER')")
    public ResponseEntity payment(@PathVariable("id") Long merchantId, @PathVariable("paymentId") Long paymentId) {
        return ResponseEntity.ok(
                getMerchantPayment.apply(merchantId, paymentId)
        );
    }
}
