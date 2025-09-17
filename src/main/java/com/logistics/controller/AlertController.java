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

    @GetMapping("/print")
    public ResponseEntity<String> printSampleLabel() {
        try {
            String zpl =
                    "^XA\r\n" +
                            "^PW600\r\n" +
                            "^LL500\r\n" +
                            "\r\n" +
                            "^FO30,20^A0N,40,40^FDVIMENPAQ^FS\r\n" +
                            "^FO30,45^GB270,2,2^FS\r\n" +
                            "\r\n" +
                            "^FO30,70^A0N,30,30^FDFROM:^FS\r\n" +
                            "^FO30,95^A0N,25,25^FDVimenpaq Logistics^FS\r\n" +
                            "^FO30,115^A0N,25,25^FD123 Industrial Area^FS\r\n" +
                            "^FO30,135^A0N,25,25^FDLudhiana, Punjab 141001^FS\r\n" +
                            "^FO30,155^A0N,25,25^FDPhone: +91-161-2345678^FS\r\n" +
                            "\r\n" +
                            "^FO30,190^A0N,30,30^FDTO:^FS\r\n" +
                            "^FO30,215^A0N,25,25^FDCustomer Name^FS\r\n" +
                            "^FO30,235^A0N,25,25^FDCustomer Address^FS\r\n" +
                            "^FO30,255^A0N,25,25^FDCity, State, PIN^FS\r\n" +
                            "\r\n" +
                            "^FO30,290^A0N,30,30^FDPACKAGE ID:^FS\r\n" +
                            "^FO30,315^A0N,40,40^FDPKG123456789^FS\r\n" +
                            "\r\n" +
                            "^FO30,350^BY2,2,60^BCN,60,Y,N^FDPKG123456789^FS\r\n" +
                            "\r\n" +
                            "^FO30,420^A0N,25,25^FDTrack: www.vimenpaq.com^FS\r\n" +
                            "\r\n" +
                            "^XZ\r\n";
            PrintService printService = printerStatusService.findZebraPrinter();

            if (printService == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Zebra printer not found");
            }

            DocPrintJob job = printService.createPrintJob();

            // Option 1: Use INPUT_STREAM.AUTOSENSE (recommended for raw commands)
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(
//                    content.getBytes(StandardCharsets.UTF_8)
//            );
//            Doc doc = new SimpleDoc(inputStream, flavor, null);
//
//            // Add print attributes
//            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
//            attributes.add(new Copies(1));
//
//            System.out.println("Sending to printer:\n" + content);
          //  job.print(doc, attributes);

            return ResponseEntity.ok(zpl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Print failed: " + e.getMessage());
        }
    }

}
