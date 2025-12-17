package com.example.eam.WorkOrder.Dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class WorkOrderLaborEntryResponse {

    private Long id;
    private Long technicianId;
    private String technicianName;
    private BigDecimal laborHours;
    private BigDecimal hourlyRate;
    private BigDecimal laborCost;
    private LocalDate laborDate;
    private String notes;
}
