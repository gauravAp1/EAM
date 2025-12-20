package com.example.eam.Procurement.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderLineResponse {

    private Long id;
    private Long itemId;
    private BigDecimal orderedQty;
    private BigDecimal receivedQty;
    private BigDecimal unitPrice;
    private String uom;
    private String remarks;
}
