package com.example.eam.WorkOrder.Dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WorkOrderMaterialUsageResponse {

    private Long id;
    private Long inventoryItemId;
    private String itemId;
    private String itemName;
    private Integer quantityUsed;
    private BigDecimal unitCostSnapshot;
    private BigDecimal totalCostSnapshot;
    private String notes;
}
