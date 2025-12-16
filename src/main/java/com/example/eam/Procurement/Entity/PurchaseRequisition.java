package com.example.eam.Procurement.Entity;

import com.example.eam.Enum.PriorityLevel;
import com.example.eam.Enum.PrRequiredForType;
import com.example.eam.Enum.PrStatus;
import com.example.eam.VendorManagement.Entity.Vendor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "purchase_requisitions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pr_pr_id", columnNames = "pr_id")
        },
        indexes = {
                @Index(name = "idx_pr_deleted", columnList = "deleted"),
                @Index(name = "idx_pr_pr_id", columnList = "pr_id"),
                @Index(name = "idx_pr_status", columnList = "status"),
                @Index(name = "idx_pr_required_by", columnList = "required_by_date")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB primary key

    @Column(name = "pr_id", nullable = false, unique = true, length = 64)
    private String prId; // Business PR ID (auto-generated)

    @Column(name = "requester", nullable = false, length = 150)
    private String requester; // user lookup (string for now)

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "required_by_date", nullable = false)
    private LocalDate requiredByDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 16)
    private PriorityLevel priority; // (validate in service: only LOW/MEDIUM/HIGH)

    // Optional header metadata (useful in real world)
    @Column(name = "department", length = 120)
    private String department;

    @Column(name = "cost_center", length = 80)
    private String costCenter;

    @Column(name = "currency", length = 10)
    private String currency; // e.g. "USD", "INR"

    @Lob
    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_vendor_id")
    private Vendor preferredVendor;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_for_type", length = 32)
    private PrRequiredForType requiredForType;

    @Column(name = "required_for_reference", length = 120)
    private String requiredForReference; // WO ID / Asset ID / Project code

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    @Builder.Default
    private PrStatus status = PrStatus.DRAFT;

    @Column(name = "total_estimated_cost", precision = 18, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalEstimatedCost = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @OneToMany(mappedBy = "purchaseRequisition", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PurchaseRequisitionLine> lines = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void addLine(PurchaseRequisitionLine line) {
        line.setPurchaseRequisition(this);
        this.lines.add(line);
    }
}
