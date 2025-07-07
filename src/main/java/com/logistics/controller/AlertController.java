package com.logistics.controller;

import com.logistics.service.AlertEmitterService;
import com.logistics.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.print.*;
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

    @PostMapping("/print")
    public ResponseEntity<String> printSampleLabel(@RequestBody String content) {
        try {
            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

            if (printService == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No default printer found");
            }

            DocPrintJob job = printService.createPrintJob();
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

            Doc doc = new SimpleDoc(content.getBytes(), flavor, null);
            job.print(doc, null);

            return ResponseEntity.ok("Print job sent successfully to " + printService.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Print failed: " + e.getMessage());
        }
    }

}
