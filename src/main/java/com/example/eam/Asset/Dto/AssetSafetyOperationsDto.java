package com.example.eam.Asset.Dto;

import lombok.Data;

@Data
public class AssetSafetyOperationsDto {

    private Boolean safetyCritical;
    private String safetyNotes;
    private String operatingInstructions;
}

