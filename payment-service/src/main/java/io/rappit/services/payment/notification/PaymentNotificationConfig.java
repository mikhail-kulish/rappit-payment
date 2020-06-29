package io.rappit.services.payment.notification;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.rappit.services.customer.api.Customers;
import io.rappit.services.merchant.api.Merchants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class PaymentNotificationConfig {
    @Bean
    public PaymentNotification paymentNotification(Customers customers, Merchants merchants, ObjectMapper mapper, JdbcTemplate jdbcTemplate) {
        return new PaymentNotification(customers, merchants, mapper, jdbcTemplate);
    }
}
