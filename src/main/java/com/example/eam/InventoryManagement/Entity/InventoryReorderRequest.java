package com.example.eam.InventoryManagement.Entity;

import com.example.eam.Enum.ReorderStatus;
import com.example.eam.VendorManagement.Entity.Vendor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory_reorder_requests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_inventory_reorder_requests_reorder_id", columnNames = "reorder_id")
        },
        indexes = {
                @Index(name = "idx_reorder_item_id", columnList = "item_db_id"),
                @Index(name = "idx_reorder_vendor_id", columnList = "vendor_id")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReorderRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Business reorder code
    @Column(name = "reorder_id", nullable = false, unique = true, length = 64)
    private String reorderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_db_id", nullable = false)
    private InventoryItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_cost_snapshot", precision = 19, scale = 2)
    private BigDecimal unitCostSnapshot;

    @Column(name = "total_cost_snapshot", precision = 19, scale = 2)
    private BigDecimal totalCostSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ReorderStatus status;

    @Column(name = "requested_by", length = 120)
    private String requestedBy;

    @Column(name = "delivery_location", length = 255)
    private String deliveryLocation;

    @Column(name = "note", length = 1000)
    private String note;

    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;
}

