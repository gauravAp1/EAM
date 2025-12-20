package com.example.eam.Procurement.Dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoLineOverrideRequest {

    @NotNull
    private Long mrLineId;

    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal unitPrice;

    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal orderedQty;

    private String uom;

    private String remarks;
}
