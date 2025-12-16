package com.example.eam.FailureCode.Dto;

import lombok.Data;

@Data
public class FailureCodeUpdateDto {

    private String failureSymptomCode;  // e.g. NOISE, OVERHEAT

    private String symptomDescription;  // Description of the symptom

    private String failureCauseCode;  // e.g. BEARING FAILURE

    private String causeDescription;  // Description of the cause

    private String actionCode;  // e.g. REPLACE PART, REPAIR

    private String actionDescription;  // Description of the action
}
