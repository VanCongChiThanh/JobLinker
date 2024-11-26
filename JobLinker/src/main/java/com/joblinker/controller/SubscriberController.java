package com.joblinker.controller;

import com.joblinker.domain.Subscriber;
import com.joblinker.service.SubscriberService;
import com.joblinker.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }
    @PostMapping("/subscribers")
    @ApiMessage("create subscriber")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber subscriber){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriber));
    }
    @PutMapping("/subcribers/{id}")
    public ResponseEntity<Subscriber> update
            (@PathVariable Long id,
             @Valid @RequestBody Subscriber subscriber){
        return ResponseEntity.ok().body(this.subscriberService.update(id, subscriber));
    }
}