package com.example.eam.Procurement.Dto;

import com.example.eam.Enum.PrLineType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrLineCreateDto {

    @NotNull
    private PrLineType lineType; // PART / ASSET / SERVICE

    // required when lineType = PART
    private Long inventoryItemDbId;

    // required when lineType = ASSET
    private Long assetDbId;

    // required when lineType = SERVICE (and optional for PART/ASSET)
    private String description;

    // optional (defaults to EACH if not sent)
    private String uom;

    @NotNull
    @Min(1)
    private Integer qtyRequested;

    private BigDecimal estimatedUnitPrice;
}
