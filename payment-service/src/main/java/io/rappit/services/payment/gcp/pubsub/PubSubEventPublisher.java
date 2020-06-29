package io.rappit.services.payment.gcp.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import io.rappit.common.media.Printable;
import io.rappit.services.payment.api.Order;
import io.rappit.services.payment.api.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PubSubEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(PubSubEventPublisher.class);
    private final static Executor executor = Executors.newSingleThreadExecutor();
    private final Publisher paymentPublisher;
    private final Publisher orderPublisher;
    private final ObjectMapper mapper;

    public PubSubEventPublisher(ObjectMapper mapper) throws IOException {
        this.mapper = mapper;
        this.paymentPublisher = Publisher.newBuilder(ProjectTopicName.of("rappit", "payment.out.PaymentStateChange")).build();
        this.orderPublisher = Publisher.newBuilder(ProjectTopicName.of("rappit", "payment.out.OrderCreated")).build();
    }

    @EventListener
    public void onApplicationEvent(Payment event) {
        log.info("Going to publish message: {}", event.getClass().getSimpleName());
        try {
            send(event, paymentPublisher);
        } catch (final Exception e) {
            log.warn("Fail to send event message", e);
        }
    }

    @EventListener
    public void onApplicationEvent(Order event) {
        log.info("Going to publish message: {}", event.getClass().getSimpleName());
        try {
            send(event, orderPublisher);
        } catch (final Exception e) {
            log.warn("Fail to send event message", e);
        }
    }

    private void send(final Printable printable, final Publisher publisher) throws Exception {
        log.debug("Going to publish message[{}]: {}", printable.getClass().getSimpleName(), mapper.writeValueAsString(printable));
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFrom(mapper.writeValueAsBytes(printable))).build();
        executor.execute(() -> publisher.publish(pubsubMessage));
    }
}
