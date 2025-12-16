package com.example.eam.FailureCode.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FailureCodeResponseDto {

    private Long id;

    private String failureSymptomCode;

    private String symptomDescription;

    private String failureCauseCode;

    private String causeDescription;

    private String actionCode;

    private String actionDescription;
}
