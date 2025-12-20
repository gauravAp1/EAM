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
public class MaterialRequisitionLineRequest {

    @NotNull
    private Long itemId;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal requestedQty;

    private String uom;

    private String remarks;
}
