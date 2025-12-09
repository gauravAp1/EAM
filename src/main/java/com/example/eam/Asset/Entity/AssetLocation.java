package com.example.eam.Asset.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "asset_location")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "asset_id", nullable = false, unique = true)
    private Asset asset;

    @Column(name = "primary_location", nullable = false, length = 255)
    private String primaryLocation;

    @Column(name = "functional_location", length = 255)
    private String functionalLocation;

    @Column(name = "department", length = 255)
    private String department;

    @Column(name = "cost_center", length = 128)
    private String costCenter;

    @Column(name = "assigned_owner", length = 255)
    private String assignedOwner;

    @Column(name = "maintenance_team", length = 255)
    private String maintenanceTeam;
}

