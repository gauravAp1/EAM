package com.example.eam.FailureCode.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FailureCodeCreateDto {

    @NotBlank
    private String failureSymptomCode; // Example: NOISE, OVERHEAT

    private String symptomDescription; // Detailed description of the symptom

    @NotBlank
    private String failureCauseCode; // Example: BEARING FAILURE

    private String causeDescription; // Detailed description of the cause

    @NotBlank
    private String actionCode; // Example: REPLACE PART, REPAIR

    private String actionDescription; // Detailed description of the action
}

