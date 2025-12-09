package com.example.eam.Asset.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "asset_safety_operations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetSafetyOperations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "asset_id", nullable = false, unique = true)
    private Asset asset;

    @Column(name = "safety_critical")
    private Boolean safetyCritical;

    @Column(name = "safety_notes", length = 2000)
    private String safetyNotes;

    @Column(name = "operating_instructions", length = 4000)
    private String operatingInstructions;
}

