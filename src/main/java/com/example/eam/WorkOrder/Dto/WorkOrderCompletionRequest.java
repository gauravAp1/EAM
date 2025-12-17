package com.example.eam.WorkOrder.Dto;

import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WorkOrderCompletionRequest {

    private LocalDateTime actualStartDateTime;
    private LocalDateTime actualEndDateTime;
    private String completionNotes;
    private String failureCause;
    private String remedyAction;
    private String beforePhotoUrl;
    private String afterPhotoUrl;

    @Valid
    private List<WorkOrderLaborEntryRequest> laborEntries;

    @Valid
    private List<WorkOrderMaterialUsageRequest> materialsUsed;
}
