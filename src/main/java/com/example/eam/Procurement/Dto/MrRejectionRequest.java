package com.example.eam.Procurement.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MrRejectionRequest {

    @NotBlank
    private String rejectedByUserId;

    @NotBlank
    private String reason;
}
