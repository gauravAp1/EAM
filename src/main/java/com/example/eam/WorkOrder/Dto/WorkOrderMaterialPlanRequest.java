package com.example.eam.WorkOrder.Dto;

import lombok.Data;

@Data
public class WorkOrderMaterialPlanRequest {

    private Long inventoryItemId;
    private Integer quantity;
    private String notes;
}
