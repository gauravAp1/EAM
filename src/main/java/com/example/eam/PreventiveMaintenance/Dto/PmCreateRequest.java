package com.example.eam.PreventiveMaintenance.Dto;

import com.example.eam.Enum.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PmCreateRequest {

    @NotBlank
    private String pmName;

    @NotNull
    private PmType pmType;

    // Applies To
    @NotNull
    private PmAppliesToType appliesToType;

    private Long assetDbId;        // required if appliesToType = ASSET
    private String assetCategory;  // required if appliesToType = CATEGORY

    @NotNull
    private LocalDate planStartDate;

    private LocalDate planEndDate;

    @NotNull
    private PmFrequencyType frequencyType;

    @NotNull
    @Min(1)
    private Integer frequencyValue;

    private TimeFrequencyUnit timeUnit;   // required if TIME_BASED
    private MeterFrequencyUnit meterUnit; // required if METER_BASED

    private Integer graceDays;

    @NotNull
    private Boolean autoGenerateWo;

    private Integer leadTimeDays;

    @NotNull
    private WorkType linkedWorkType;

    @NotNull
    private PriorityLevel defaultPriority;
}

