package io.rappit.services.payment.controller.v1.portal;

import io.rappit.common.media.Printable;
import io.rappit.services.payment.command.statistics.JdbcMerchantOrderStat;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/portal/merchant/{merchantId}/dashboard")
@CrossOrigin(origins = {"https://rappitpay.com", "http://localhost:3000"}, allowCredentials = "true")
public class MerchantDashboardController {
    private final JdbcMerchantOrderStat merchantOrderStat;

    public MerchantDashboardController(JdbcTemplate jdbcTemplate) {
        this.merchantOrderStat = new JdbcMerchantOrderStat(jdbcTemplate);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER')")
    public ResponseEntity orders(@PathVariable("merchantId") Long merchantId) {
        return ResponseEntity.ok(
                (Printable) media -> media.with("orders", merchantOrderStat.apply(merchantId))
        );
    }
}
