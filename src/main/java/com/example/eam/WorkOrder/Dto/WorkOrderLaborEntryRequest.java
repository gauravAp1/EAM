package com.example.eam.WorkOrder.Dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WorkOrderLaborEntryRequest {

    private Long technicianId;
    private BigDecimal laborHours;
    private BigDecimal hourlyRate;
    private LocalDate laborDate;
    private String notes;
}
