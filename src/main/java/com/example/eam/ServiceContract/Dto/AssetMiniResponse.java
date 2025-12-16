package com.example.eam.ServiceContract.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssetMiniResponse {
    private Long assetDbId;
    private String assetId;   // business assetId
    private String assetName;
}

