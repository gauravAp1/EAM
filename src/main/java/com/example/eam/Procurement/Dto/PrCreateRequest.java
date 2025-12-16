package com.example.eam.Procurement.Dto;

import com.example.eam.Enum.PriorityLevel;
import com.example.eam.Enum.PrRequiredForType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PrCreateRequest {

    @NotBlank
    private String requester;

    // optional but useful for filtering/reporting
    private String department;
    private String costCenter;

    // optional - e.g. "USD", "INR"
    private String currency;

    // optional
    private String notes;

    // optional preferred vendor
    private Long preferredVendorId;

    @NotNull
    private LocalDate requiredByDate;

    @NotNull
    private PriorityLevel priority; // LOW/MEDIUM/HIGH (CRITICAL not allowed)

    private PrRequiredForType requiredForType;
    private String requiredForReference;

    @Valid
    @NotEmpty
    private List<PrLineCreateDto> lines;
}
