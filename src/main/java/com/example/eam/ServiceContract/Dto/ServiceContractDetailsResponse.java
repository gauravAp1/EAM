package com.example.eam.ServiceContract.Dto;

import com.example.eam.Enum.CoverageType;
import com.example.eam.Enum.SlaTimeUnit;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ServiceContractDetailsResponse {
    private Long id;
    private String contractId;

    private String contractName;

    private Long vendorDbId;
    private String vendorId;     // if you have business vendorId
    private String vendorName;

    private LocalDate startDate;
    private LocalDate endDate;

    private CoverageType coverageType;

    private Integer responseTimeSlaValue;
    private SlaTimeUnit responseTimeSlaUnit;

    private BigDecimal uptimeSlaPercent;

    private String notes;

    private Set<AssetMiniResponse> coveredAssets;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

