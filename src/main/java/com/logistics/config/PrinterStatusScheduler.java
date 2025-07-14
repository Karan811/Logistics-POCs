package com.logistics.config;

import com.logistics.service.PrinterStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@Component
public class PrinterStatusScheduler {
    @Autowired
    private PrinterStatusService printerStatusService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 5000) // or trigger this manually
    public void sendPrinterStatus() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

        List<Map<String, String>> printers = Arrays.stream(services)
                .map(service -> Map.of(
                        "name", service.getName(),
                        "supportsZPL", Boolean.toString(service.isDocFlavorSupported(DocFlavor.BYTE_ARRAY.AUTOSENSE))
                ))
                .collect(Collectors.toList());

        messagingTemplate.convertAndSend("/topic/printer-status", printers);
    }
}
