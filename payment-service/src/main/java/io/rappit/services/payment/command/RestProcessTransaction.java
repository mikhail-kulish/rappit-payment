package io.rappit.services.payment.command;

import io.rappit.services.processor.api.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.function.Function;

public class RestProcessTransaction implements Function<Transaction, Object> {
    private final RestTemplate restTemplate;

    public RestProcessTransaction(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Object apply(Transaction transaction) {
        return restTemplate.postForEntity(
                URI.create("http://processor-lb/processor/process"),
                transaction,
                Object.class
        ).getBody();
    }
}
