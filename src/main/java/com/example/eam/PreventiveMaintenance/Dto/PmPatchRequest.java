package com.example.eam.PreventiveMaintenance.Dto;

import com.example.eam.Enum.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PmPatchRequest {
    private String pmName;
    private PmType pmType;

    private PmAppliesToType appliesToType;
    private Long assetDbId;
    private String assetCategory;

    private LocalDate planStartDate;
    private LocalDate planEndDate;

    private PmFrequencyType frequencyType;
    private Integer frequencyValue;
    private TimeFrequencyUnit timeUnit;
    private MeterFrequencyUnit meterUnit;

    private Integer graceDays;
    private Boolean autoGenerateWo;
    private Integer leadTimeDays;

    private WorkType linkedWorkType;
    private PriorityLevel defaultPriority;

    private Boolean active;
}

