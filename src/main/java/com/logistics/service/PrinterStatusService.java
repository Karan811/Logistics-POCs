package com.logistics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

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
        String status = checkPrinterAvailability().equals("false") ? "Printer is offline" : checkPrinterAvailability();
        broadcastPrinterStatus(status);
    }

    private String checkPrinterAvailability() {
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();

        if (service != null) {
            System.out.println("Default printer: " + service.getName());
            if(service.isDocFlavorSupported(DocFlavor.BYTE_ARRAY.AUTOSENSE)){
                return service.getName();
            }
        } else {
            System.out.println("No default printer found.");
            return "false";
        }
        return "false";
    }
}
