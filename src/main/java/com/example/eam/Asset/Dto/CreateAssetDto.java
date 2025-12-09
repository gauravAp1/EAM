package com.example.eam.Asset.Dto;


import com.example.eam.Enum.AssetCriticality;
import com.example.eam.Enum.AssetStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAssetDto {

    @NotBlank
    private String assetId;          // Asset ID*

    @NotBlank
    private String assetName;        // Asset Name*

    private String shortDescription;

    @NotBlank
    private String assetCategory;    // Asset Category*

    private String assetType;

    private Long parentAssetId;      // Parent Asset (optional)

    @NotNull
    private AssetStatus status;      // Status*

    private AssetCriticality criticality;
    private String ownership;
    private String assetTag;         // Tag / Barcode / RFID
}

