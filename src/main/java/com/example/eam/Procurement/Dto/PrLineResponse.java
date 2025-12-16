package com.example.eam.Procurement.Dto;

import com.example.eam.Enum.PrLineType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PrLineResponse {

    private Long id;

    private PrLineType lineType;

    private Long inventoryItemDbId;
    private String itemId;
    private String itemName;

    private Long assetDbId;
    private String assetId;
    private String assetName;

    private String description;
    private String uom;

    private Integer qtyRequested;
    private BigDecimal estimatedUnitPrice;
    private BigDecimal lineTotal;
}
