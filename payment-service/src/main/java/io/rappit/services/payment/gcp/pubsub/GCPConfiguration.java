package io.rappit.services.payment.gcp.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("gcp")
public class GCPConfiguration {
    @Bean
    public PubSubEventPublisher gcpEventPublisher(ObjectMapper mapper) throws Exception {
        return new PubSubEventPublisher(mapper);
    }

}
