package com.cart.ecom_proj.controller;

import com.cart.ecom_proj.service.AlertEmitterService;
import com.cart.ecom_proj.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AlertController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();


    private final ProductService    service;
    private final AlertEmitterService alertEmitterService;
    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        return alertEmitterService.subscribe();
    }

    @PostMapping("/updateStatus")
    public void sendAlert(@RequestBody Long packageId) {
        service.updateStatus(packageId, "lost");
    }
}
