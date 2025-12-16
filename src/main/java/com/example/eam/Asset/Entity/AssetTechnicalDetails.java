package com.example.eam.Asset.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "asset_technical_details")
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetTechnicalDetails that = (AssetTechnicalDetails) o;
        if (this.id != null && that.id != null) {
            return this.id.equals(that.id);
        }
        return this.asset != null && that.asset != null && this.asset.equals(that.asset);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
