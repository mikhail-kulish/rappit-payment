package io.rappit.services.payment.controller.v1.portal;

import io.rappit.services.payment.api.filter.Pageable;
import io.rappit.services.payment.command.JdbcGetCustomerPayments;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController("portalCustomerController")
@RequestMapping("/v1/portal/customer")
@CrossOrigin(origins = {"https://rappitpay.com", "http://localhost:3000"}, allowCredentials = "true")
@PreAuthorize("hasAuthority('ADMIN')")
public class CustomerController {
    private final JdbcGetCustomerPayments getCustomerPayments;

    public CustomerController(JdbcTemplate jdbcTemplate) {
        this.getCustomerPayments = new JdbcGetCustomerPayments(jdbcTemplate);
    }

    @GetMapping("/{id}/payment")
    public ResponseEntity payments(@PathVariable("id") String customerId,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                   @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(getCustomerPayments.apply(customerId, new Pageable.Simple(page, size)));
    }
}
