package com.example.eam.WorkOrder.Entity;

import com.example.eam.Technician.Entity.Technician;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "work_order_labor_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderLaborEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private Technician technician;

    @Column(name = "technician_name", length = 200)
    private String technicianNameSnapshot;

    @Column(name = "labor_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal laborHours;

    @Column(name = "hourly_rate", precision = 19, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "labor_cost", precision = 19, scale = 2)
    private BigDecimal laborCost;

    @Column(name = "labor_date")
    private LocalDate laborDate;

    @Lob
    @Column(name = "labor_notes")
    private String notes;
}
