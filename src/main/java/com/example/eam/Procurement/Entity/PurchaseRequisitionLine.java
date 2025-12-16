package com.example.eam.Procurement.Entity;

import com.example.eam.Asset.Entity.Asset;
import com.example.eam.Enum.PrLineType;
import com.example.eam.InventoryManagement.Entity.InventoryItem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "purchase_requisition_lines",
        indexes = {
                @Index(name = "idx_pr_line_pr_id", columnList = "purchase_requisition_id")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequisitionLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_requisition_id", nullable = false)
    private PurchaseRequisition purchaseRequisition;

    @Enumerated(EnumType.STRING)
    @Column(name = "line_type", nullable = false, length = 16)
    private PrLineType lineType; // PART / ASSET / SERVICE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id")
    private InventoryItem inventoryItem; // for PART

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset; // for ASSET

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "uom", length = 30)
    private String uom; // EACH / LITER / METER etc.

    @Column(name = "qty_requested", nullable = false)
    private Integer qtyRequested;

    @Column(name = "estimated_unit_price", precision = 18, scale = 2)
    private BigDecimal estimatedUnitPrice;

    @Column(name = "line_total", precision = 18, scale = 2)
    private BigDecimal lineTotal; // computed in service
}
