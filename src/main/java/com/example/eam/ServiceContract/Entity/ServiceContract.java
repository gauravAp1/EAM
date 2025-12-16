package com.example.eam.ServiceContract.Entity;

import com.example.eam.Asset.Entity.Asset;
import com.example.eam.Enum.CoverageType;
import com.example.eam.Enum.SlaTimeUnit;
import com.example.eam.VendorManagement.Entity.Vendor; 
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "service_contracts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_service_contract_contract_id", columnNames = "contract_id")
        },
        indexes = {
                @Index(name = "idx_service_contract_deleted", columnList = "deleted"),
                @Index(name = "idx_service_contract_vendor", columnList = "vendor_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB PK

    @Column(name = "contract_id", nullable = false, unique = true, length = 64)
    private String contractId; // Business ID (auto)

    @Column(name = "contract_name", nullable = false, length = 255)
    private String contractName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor; // Lookup to Vendor

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "coverage_type", nullable = false, length = 64)
    private CoverageType coverageType;

    // SLA response time (value + unit)
    @Column(name = "response_time_sla_value")
    private Integer responseTimeSlaValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_time_sla_unit", length = 16)
    private SlaTimeUnit responseTimeSlaUnit;

    // e.g. 99.90
    @Column(name = "uptime_sla_percent", precision = 5, scale = 2)
    private BigDecimal uptimeSlaPercent;

    @Lob
    @Column(name = "notes")
    private String notes;

    // Covered assets
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "service_contract_assets",
            joinColumns = @JoinColumn(name = "service_contract_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id"),
            indexes = {
                    @Index(name = "idx_sca_contract", columnList = "service_contract_id"),
                    @Index(name = "idx_sca_asset", columnList = "asset_id")
            }
    )
    @Builder.Default
    private Set<Asset> coveredAssets = new LinkedHashSet<>();

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceContract that = (ServiceContract) o;
        if (this.id != null && that.id != null) {
            return this.id.equals(that.id);
        }
        return this.contractId != null && this.contractId.equals(that.contractId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
