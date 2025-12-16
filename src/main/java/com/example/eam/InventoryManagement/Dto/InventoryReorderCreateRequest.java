package com.example.eam.InventoryManagement.Dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class InventoryReorderCreateRequest {

    // Optional: override vendor (if null -> use primary vendor)
    private Long vendorDbId;

    // Optional: if null -> use item.reorderQuantity
    @Min(1)
    private Integer quantity;

    private String requestedBy;
    private String deliveryLocation;
    private String note;
}

