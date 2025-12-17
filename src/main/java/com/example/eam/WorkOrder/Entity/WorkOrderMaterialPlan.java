package com.example.eam.WorkOrder.Entity;

import com.example.eam.InventoryManagement.Entity.InventoryItem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "work_order_material_plan")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderMaterialPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Column(name = "quantity_planned", nullable = false)
    private Integer quantityPlanned;

    @Column(name = "unit_cost_snapshot", precision = 19, scale = 2)
    private BigDecimal unitCostSnapshot;

    @Column(name = "total_cost_snapshot", precision = 19, scale = 2)
    private BigDecimal totalCostSnapshot;

    @Lob
    @Column(name = "plan_notes")
    private String notes;
}
