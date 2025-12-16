package com.example.eam.InventoryManagement.Dto;

import com.example.eam.Enum.ReorderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InventoryReorderResponse {
    private Long id;
    private String reorderId;
    private Long itemDbId;
    private String itemId;
    private String itemName;

    private Long vendorDbId;
    private String vendorName;
    private String vendorEmail;

    private Integer quantity;
    private ReorderStatus status;
    private LocalDateTime requestedAt;

    private String requestedBy;
    private String deliveryLocation;
    private String note;
}

