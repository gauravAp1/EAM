package com.example.eam.ServiceContract.Dto;

import com.example.eam.Enum.CoverageType;
import com.example.eam.Enum.SlaTimeUnit;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class ServiceContractPatchRequest {

    private String contractName;
    private Long vendorId;

    private LocalDate startDate;
    private LocalDate endDate;

    private CoverageType coverageType;

    private Integer responseTimeSlaValue;
    private SlaTimeUnit responseTimeSlaUnit;

    private BigDecimal uptimeSlaPercent;

    private String notes;

    // If provided => replace covered assets
    private Set<Long> coveredAssetDbIds;
}

