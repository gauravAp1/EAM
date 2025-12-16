package com.example.eam.PreventiveMaintenance.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "pm_schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreventiveMaintenanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pm_template_id", nullable = false, unique = true)
    private PreventiveMaintenance template;

    @Column(name = "next_due_date")
    private LocalDate nextDueDate; // used for TIME_BASED

    @Column(name = "last_generated_due_date")
    private LocalDate lastGeneratedDueDate; // used for TIME_BASED
}

