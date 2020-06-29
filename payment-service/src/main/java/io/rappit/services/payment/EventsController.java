package io.rappit.services.payment;

import io.rappit.services.account.api.RestAccounts;
import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.command.JdbcApplyReceipt;
import io.rappit.services.processor.api.TransactionStateChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/_events")
public class EventsController {

    private final static Logger log = LoggerFactory.getLogger(EventsController.class);
    private final RestTemplate restTemplate;
    private final JdbcApplyReceipt applyReceipt;

    public EventsController(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, RestTemplate restTemplate, ApplicationEventPublisher eventPublisher) {
        this.restTemplate = restTemplate;
        this.applyReceipt = new JdbcApplyReceipt(jdbcTemplate, transactionTemplate, eventPublisher);
    }

    @PostMapping("/TransactionStateChange")
    public ResponseEntity onTransactionStateChange(@RequestBody TransactionStateChangeEvent event) {
        log.info("Got event with id={} timestamp={} data=[{}:{}]", event.id(), event.published(), event.receipt().reference(), event.receipt().status());
        Payment payment = applyReceipt.apply(event.receipt());
        // TODO: move it to account-service
        try {
            event.receipt().account().ifPresent(account -> {
                if (account.type().equals("TOKEN") && account.id().isEmpty()) {
                    new RestAccounts(restTemplate, payment.customer()).add(account);
                }
            });
        } catch (final Exception e) {
            log.warn("Error adding tokenized account for receipt" + event.receipt().reference(), e);
        }
        return ResponseEntity.ok().build();
    }
}
