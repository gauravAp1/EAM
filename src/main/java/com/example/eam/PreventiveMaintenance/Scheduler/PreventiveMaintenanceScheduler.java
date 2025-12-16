package com.example.eam.PreventiveMaintenance.Scheduler;

import com.example.eam.PreventiveMaintenance.Service.PreventiveMaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreventiveMaintenanceScheduler {

    private final PreventiveMaintenanceService service;

    // every 30 minutes (adjust as needed)
    @Scheduled(cron = "0 */30 * * * *")
    public void generateWorkOrders() {
        service.generateDueWorkOrders();
    }
}

