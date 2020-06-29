package io.rappit.services.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception
    {
//        System.out.println(ServiceAccountCredentials.fromStream(new FileInputStream("/Users/mikhailo.kulish/development/rappit/services/payment/payment-service/rappit-b94687c38f51.json")).refreshAccessToken());
//        System.exit(0);
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(Application.class, args);
    }
}
