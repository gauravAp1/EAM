package com.example.eam.WorkOrder.Dto;

import lombok.Data;

@Data
public class WorkOrderMaterialUsageRequest {

    private Long inventoryItemId;
    private Integer quantityUsed;
    private String notes;
}
