package com.example.eam.Procurement.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvertMrToPoRequest {

    @NotNull
    private Long vendorId;

    @NotBlank
    private String createdByUserId;

    private LocalDate expectedDeliveryDate;

    private String remarks;

    @Valid
    private List<PoLineOverrideRequest> lineOverrides;
}
