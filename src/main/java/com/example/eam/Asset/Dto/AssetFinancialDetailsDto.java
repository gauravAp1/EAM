package com.example.eam.Asset.Dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.eam.Enum.DepreciationMethod;

@Data
public class AssetFinancialDetailsDto {

    @NotNull
    private LocalDate acquisitionDate;   // Acquisition Date*

    @NotNull
    private BigDecimal acquisitionCost;  // Acquisition Cost*

    private String supplier;
    private String poInvoiceNumber;
    private DepreciationMethod depreciationMethod;
    private Integer usefulLifeYears;
    private LocalDate depreciationStartDate;
    private BigDecimal salvageValue;
    private BigDecimal accumulatedDepreciation;
    private BigDecimal currentBookValue;
}

