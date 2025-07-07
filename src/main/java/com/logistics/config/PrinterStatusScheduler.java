package com.logistics.config;

import com.logistics.service.PrinterStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Configuration
@EnableScheduling
@Component
public class PrinterStatusScheduler {
    @Autowired
    private PrinterStatusService printerStatusService;

    @Scheduled(fixedRate = 5000)
    public void pingPrinter() {
        printerStatusService.checkAndUpdateStatus();
    }
}
