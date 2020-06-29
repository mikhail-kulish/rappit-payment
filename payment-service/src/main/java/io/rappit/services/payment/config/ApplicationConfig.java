package io.rappit.services.payment.config;

import io.rappit.common.media.Printable;
import io.rappit.common.media.jackson.PrintableSerializer;
import io.rappit.services.account.api.AccountDeserializer;
import io.rappit.services.account.api.CardAccount;
import io.rappit.services.customer.api.Customers;
import io.rappit.services.customer.firebase.GCPCustomers;
import io.rappit.services.merchant.api.Merchant;
import io.rappit.services.merchant.api.MerchantDeserializer;
import io.rappit.services.merchant.api.Merchants;
import io.rappit.services.merchant.api.rest.RestMerchants;
import io.rappit.services.payment.EventPublishingPayments;
import io.rappit.services.payment.LoggingDeserializer;
import io.rappit.services.payment.Payments;
import io.rappit.services.payment.api.OrderForm;
import io.rappit.services.payment.api.Payer;
import io.rappit.services.payment.api.PayerDeserializer;
import io.rappit.services.payment.auth.*;
import io.rappit.services.payment.jdbc.JdbcPayments;
import io.rappit.services.payment.json.OrderFormDeserializer;
import io.rappit.services.processor.api.Receipt;
import io.rappit.services.processor.api.ReceiptDeserializer;
import io.rappit.services.processor.api.TransactionStateChangeEvent;
import io.rappit.services.processor.api.json.TransactionStateChangeDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizeJackson() {
        return builder -> builder
                .serializerByType(Printable.class, new PrintableSerializer())
                .deserializerByType(OrderForm.class, new OrderFormDeserializer())
                .deserializerByType(Receipt.class, new ReceiptDeserializer())
                .deserializerByType(Payer.class, new PayerDeserializer())
                .deserializerByType(Merchant.class, new MerchantDeserializer())
                .deserializerByType(CardAccount.class, new AccountDeserializer())
                .deserializerByType(TransactionStateChangeEvent.class, new LoggingDeserializer<TransactionStateChangeEvent>(new TransactionStateChangeDeserializer()));
    }

    @Bean
    public Payments payments(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, ApplicationEventPublisher publisher) {
        return new EventPublishingPayments(
                publisher,
                new JdbcPayments(jdbcTemplate, transactionTemplate)
        );
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
//                .interceptors((request, body, execution) -> {
//                    HttpHeaders headers = request.getHeaders();
//                    if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() instanceof FirebaseAuthenticationToken) {
//                        headers.add("X-Authorization-Rappit", SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
//                    }
//                    return execution.execute(request, body);
//                })
                .build();
    }

    @Bean
    public Customers customers() {
        return new GCPCustomers();
    }

    @Bean
    public Merchants merchants(RestTemplate restTemplate) {
        return new RestMerchants(restTemplate);
    }

    @Bean
    public MerchantAuthenticationProvider merchantAuthProvider(Merchants merchants){
        return new MerchantAuthenticationProvider(merchants);
    }

    @Bean
    public FirebaseAuthenticationProvider firebaseAuthProvider(){
        return new FirebaseAuthenticationProvider();
    }

}
