package com.logistics.controller;

import com.logistics.service.AlertEmitterService;
import com.logistics.service.PrinterStatusService;
import com.logistics.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AlertController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();


    private final ProductService    service;
    private final PrinterStatusService printerStatusService;
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
            content = "! 0 200 200 500 1\r\n" +
                    // Use font 4 for headers (straight, not italic)
                    "TEXT 4 0 30 20 VIMENPAQ\r\n" +
                    "LINE 30 45 300 45 2\r\n" +

                    // Use font 2 for section headers (medium, straight)
                    "TEXT 2 0 30 70 FROM:\r\n" +
                    // Use font 1 for regular text (small, straight)
                    "TEXT 1 0 30 95 Vimenpaq Logistics\r\n" +
                    "TEXT 1 0 30 115 123 Industrial Area\r\n" +
                    "TEXT 1 0 30 135 Ludhiana, Punjab 141001\r\n" +
                    "TEXT 1 0 30 155 Phone: +91-161-2345678\r\n" +

                    // To section with proper spacing
                    "TEXT 2 0 30 190 TO:\r\n" +
                    "TEXT 1 0 30 215 Customer Name\r\n" +
                    "TEXT 1 0 30 235 Customer Address\r\n" +
                    "TEXT 1 0 30 255 City, State, PIN\r\n" +

                    // Package details
                    "TEXT 2 0 30 290 PACKAGE ID:\r\n" +
                    "TEXT 3 0 30 315 PKG123456789\r\n" +

                    // Barcode with enough space
                    "BARCODE 128 1 1 50 30 350 PKG123456789\r\n" +

                    // Footer
                    "TEXT 1 0 30 420 Track: www.vimenpaq.com\r\n" +
                    "PRINT\r\n";
            PrintService printService = printerStatusService.findZebraPrinter();

            if (printService == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Zebra printer not found");
            }

            DocPrintJob job = printService.createPrintJob();

            // Option 1: Use INPUT_STREAM.AUTOSENSE (recommended for raw commands)
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(
                    content.getBytes(StandardCharsets.UTF_8)
            );
            Doc doc = new SimpleDoc(inputStream, flavor, null);

            // Add print attributes
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new Copies(1));

            System.out.println("Sending to printer:\n" + content);
            job.print(doc, attributes);

            return ResponseEntity.ok("Print job sent successfully to " + printService.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Print failed: " + e.getMessage());
        }
    }

}
