package com.example.eam.Procurement.Dto;

import com.example.eam.Procurement.Enum.PurchaseOrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePurchaseOrderStatusRequest {

    @NotNull
    private PurchaseOrderStatus newStatus;

    private String remarks;
}
