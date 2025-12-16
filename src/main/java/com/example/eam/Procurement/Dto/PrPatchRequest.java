package com.example.eam.Procurement.Dto;

import com.example.eam.Enum.PriorityLevel;
import com.example.eam.Enum.PrRequiredForType;
import com.example.eam.Enum.PrStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PrPatchRequest {

    private String requester;

    private String department;
    private String costCenter;
    private String currency;
    private String notes;

    private Long preferredVendorId;

    private LocalDate requiredByDate;
    private PriorityLevel priority;

    private PrRequiredForType requiredForType;
    private String requiredForReference;

    // optional: change PR workflow status
    private PrStatus status;

    // If provided -> replace all lines (service allows only in DRAFT in our service logic)
    private List<PrLineCreateDto> lines;
}
