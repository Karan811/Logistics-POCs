package com.logistics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.nio.charset.StandardCharsets;

@Service
public class PrinterStatusService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public PrinterStatusService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastPrinterStatus(String status) {
        messagingTemplate.convertAndSend("/topic/printer-status", status);
    }

    public void checkAndUpdateStatus() {
        // Simulate check
        String status = checkForZebraOrNetworkPrinter().equals("false") ? "Printer is offline" : checkForZebraOrNetworkPrinter();
        broadcastPrinterStatus(status);
    }

    private String checkForZebraOrNetworkPrinter() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService service : services) {
            System.out.println("Found printer: " + service.getName());

            if (service.getName().toLowerCase().contains("zebra") &&
                    service.isDocFlavorSupported(DocFlavor.BYTE_ARRAY.AUTOSENSE)) {
                return service.getName();
            }

            // You can also check for IP, hostname, or network keywords here
            if (service.getName().toLowerCase().contains("network") ||
                    service.getName().contains("192.") || service.getName().contains("10.")) {
                return service.getName();
            }
        }

        return "No Zebra or network printer found";
    }

    public PrintService findZebraPrinter() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService service : services) {
            if (service.getName().toLowerCase().contains("zdesigner")) {
                return service;
            }
        }
        return null;
    }





}
