package com.example.eam.Asset.Dto;


import lombok.Data;

@Data
public class AssetTechnicalDetailsDto {

    private String manufacturer;
    private String model;
    private String serialNumber;
    private Integer yearOfManufacture;
    private String powerRating;
    private String voltage;
    private String capacity;
}

