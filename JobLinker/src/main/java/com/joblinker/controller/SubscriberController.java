package com.joblinker.controller;

import com.joblinker.domain.Subscriber;
import com.joblinker.service.SubscriberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber subscriber){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriber));
    }

}