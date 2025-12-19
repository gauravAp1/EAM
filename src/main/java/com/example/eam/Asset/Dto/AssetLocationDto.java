package com.example.eam.Asset.Dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssetLocationDto {

    @NotBlank
    private String location;     

    private String department;
    private String costCenter;
    private String assignedOwner;
    private String maintenanceTeam;
}

