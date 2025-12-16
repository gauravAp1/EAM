package com.example.eam.ServiceContract.Dto;

import com.example.eam.Enum.CoverageType;
import com.example.eam.Enum.SlaTimeUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class ServiceContractCreateRequest {

    @NotBlank
    private String contractName;

    @NotNull
    private Long vendorId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private CoverageType coverageType;

    private Integer responseTimeSlaValue; // e.g. 4
    private SlaTimeUnit responseTimeSlaUnit; // HOURS/DAYS

    private BigDecimal uptimeSlaPercent; // e.g. 99.90

    private String notes;

    // DB ids of assets to link
    private Set<Long> coveredAssetDbIds;
}

