package io.rappit.services.payment.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.tasks.v2beta3.*;
import com.google.protobuf.ByteString;
import io.rappit.common.media.Printable;
import io.rappit.common.world.people.Name;
import io.rappit.services.customer.api.Customer;
import io.rappit.services.customer.api.Customers;
import io.rappit.services.merchant.api.Merchant;
import io.rappit.services.merchant.api.Merchants;
import io.rappit.services.payment.FlatMapMedia;
import io.rappit.services.payment.api.Payment;
import io.rappit.services.payment.command.JdbcGetOrderItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class PaymentNotification {
    private static final Logger log = LoggerFactory.getLogger(PaymentNotification.class);
    private static final QueueName NOTIFICATION_QUEUE = QueueName.of("rappit", "us-central1", "Notifications");
    private final ObjectMapper mapper;
    private final HttpRequest.Builder requestBuilder;
    private final Task.Builder taskBuilder;
    private final Customers customers;
    private final Merchants merchants;
    private final JdbcGetOrderItems orderItems;

    public PaymentNotification(Customers customers, Merchants merchants, ObjectMapper mapper, JdbcTemplate jdbcTemplate) {
        this.mapper = mapper;
        this.requestBuilder = HttpRequest.newBuilder()
                .setHttpMethod(HttpMethod.POST)
                .setUrl("https://api.mailjet.com/v3.1/send")
                .putHeaders("Content-type", "application/json")
                .putHeaders("Authorization", "Basic NDQyNGM5NmZmNzYyYmU0YTcyNTI4MWMxZTQxM2FhMDQ6NWY2Y2FhZWZkNjJjNjk2YjRkNTc2YzhjYWU5ZjE5NTE=");
        this.taskBuilder = Task.newBuilder();
        this.customers = customers;
        this.merchants = merchants;
        this.orderItems = new JdbcGetOrderItems(jdbcTemplate);
    }

    @EventListener
    public void onPaymentUpdated(Payment payment) {
        if (!(Payment.Status.APPROVED == payment.status() || Payment.Status.DECLINED == payment.status()) ) {
            return;
        }
        log.info("Going to send notification for Payment id={} status={}", payment.id(), payment.status());
        try (CloudTasksClient client = CloudTasksClient.create()) {
            Customer customer = customers.customer(payment.customer());
            Merchant merchant = merchants.get(payment.order().merchant());
            Integer templateId = payment.status() == Payment.Status.APPROVED ? 1228414 : 1243923;
            var task = createTask(
                    templateId, payment.payer().email(), payment.payer().name().full(),
                    payloadMedia -> payloadMedia
                            .with("payment",
                                    media -> media.with(
                                            "order",
                                            orderMedia -> orderMedia.with("id", payment.order().id())
                                                    .with("reference", payment.order().reference())
                                                    .with("amount", payment.order().amount())
                                                    .with("description", payment.order().description())
                                                    .with("items", orderItems.apply(payment.order()))
                                    ).with("id", payment.id())
                                            .with("completed", payment.completed().map(completed -> completed.atZone(ZoneId.of("America/Panama"))).map(time -> time.format(DateTimeFormatter.ISO_LOCAL_DATE) + " " + time.format(DateTimeFormatter.ISO_LOCAL_TIME)).orElse(null))
                            ).with("merchant", media -> media.with("business", merchant.business()))
                            .with("customer", media -> media.with("name", customer.name().orElse(null)))
                            .with("language", "es")
            );
            client.createTask(NOTIFICATION_QUEUE, task);
        } catch (final Exception e) {
            log.error("Error creating PaymentUpdated task for reference " + payment.id(), e);
        }

    }

    private Task createTask(Integer template, String toEmail, String toName, Printable payload) throws JsonProcessingException {
        FlatMapMedia payloadMedia = new FlatMapMedia();
        payload.print(payloadMedia);
        String output = mapper.writeValueAsString(((Printable) media ->
                media.with("Messages", Collections.singleton((Printable) message ->
                        message.with("From", p -> p.with("Email", "no-reply@rappit.io").with("Name", "rappit pay"))
                                .with("To", Collections.singleton((Printable) to -> to.with("Email", toEmail).with("Name", toName)))
                                .with("TemplateID", template)
                                .with("TemplateLanguage", true)
                                .with("Variables", payloadMedia.toPrintable())
                ))
        ));
        log.info("Going to create task {}", output);
        return taskBuilder.setHttpRequest(
                requestBuilder.setBody(
                        ByteString.copyFrom(
                                output.getBytes()
                        )
                ).build()
        ).build();
    }

}
