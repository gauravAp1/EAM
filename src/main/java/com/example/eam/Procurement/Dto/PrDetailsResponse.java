package com.example.eam.Procurement.Dto;

import com.example.eam.Enum.PriorityLevel;
import com.example.eam.Enum.PrRequiredForType;
import com.example.eam.Enum.PrStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PrDetailsResponse {

    private Long id;
    private String prId;

    private String requester;
    private LocalDate requestDate;
    private LocalDate requiredByDate;

    private PriorityLevel priority;

    private String department;
    private String costCenter;
    private String currency;
    private String notes;

    private Long preferredVendorId;
    private String preferredVendorName;

    private PrRequiredForType requiredForType;
    private String requiredForReference;

    private PrStatus status;

    private BigDecimal totalEstimatedCost;

    private List<PrLineResponse> lines;
}
