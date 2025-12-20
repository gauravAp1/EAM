package com.example.eam.Procurement.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "material_requisition_lines",
        indexes = {
                @Index(name = "idx_mr_line_mr", columnList = "material_requisition_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialRequisitionLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_requisition_id", nullable = false)
    private MaterialRequisition materialRequisition;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "requested_qty", nullable = false, precision = 19, scale = 4)
    private BigDecimal requestedQty;

    @Column(name = "uom", length = 32)
    private String uom;

    @Column(name = "remarks", length = 500)
    private String remarks;
}
