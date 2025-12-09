package com.example.eam.Asset.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "asset_technical_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetTechnicalDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "asset_id", nullable = false, unique = true)
    private Asset asset;

    @Column(name = "manufacturer", length = 255)
    private String manufacturer;

    @Column(name = "model", length = 255)
    private String model;

    @Column(name = "serial_number", length = 255)
    private String serialNumber;

    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;

    @Column(name = "power_rating", length = 128)
    private String powerRating;

    @Column(name = "voltage", length = 128)
    private String voltage;

    @Column(name = "capacity", length = 128)
    private String capacity;
}

