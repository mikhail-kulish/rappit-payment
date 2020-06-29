package io.rappit.services.payment.gcp.pubsub;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class GCPWarmupController {

    @GetMapping
    public ResponseEntity warmup(){
        return ResponseEntity.ok().build();
    }
}
