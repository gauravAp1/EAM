package com.example.eam.PreventiveMaintenance.Entity;

import com.example.eam.Asset.Entity.Asset;
import com.example.eam.Enum.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "pm_templates",
        indexes = {
                @Index(name = "idx_pm_templates_pm_id", columnList = "pm_id", unique = true)
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreventiveMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB PK

    @Column(name = "pm_id", nullable = false, unique = true, length = 64)
    private String pmId; // business id (auto-generated)

    @Column(name = "pm_name", nullable = false, length = 255)
    private String pmName;

    @Enumerated(EnumType.STRING)
    @Column(name = "pm_type", nullable = false, length = 50)
    private PmType pmType;

    // Applies To
    @Enumerated(EnumType.STRING)
    @Column(name = "applies_to_type", nullable = false, length = 20)
    private PmAppliesToType appliesToType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset; // if appliesToType = ASSET

    @Column(name = "asset_category", length = 100)
    private String assetCategory; // if appliesToType = CATEGORY

    @Column(name = "plan_start_date", nullable = false)
    private LocalDate planStartDate;

    @Column(name = "plan_end_date")
    private LocalDate planEndDate;

    // Frequency
    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_type", nullable = false, length = 20)
    private PmFrequencyType frequencyType;

    @Column(name = "frequency_value", nullable = false)
    private Integer frequencyValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_unit", length = 20)
    private TimeFrequencyUnit timeUnit; // only if TIME_BASED

    @Enumerated(EnumType.STRING)
    @Column(name = "meter_unit", length = 20)
    private MeterFrequencyUnit meterUnit; // only if METER_BASED

    @Column(name = "grace_days")
    private Integer graceDays; // ± days tolerance

    @Column(name = "auto_generate_wo", nullable = false)
    private Boolean autoGenerateWo;

    @Column(name = "lead_time_days")
    private Integer leadTimeDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "linked_work_type", nullable = false, length = 50)
    private WorkType linkedWorkType;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_priority", nullable = false, length = 50)
    private PriorityLevel defaultPriority;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;
}

