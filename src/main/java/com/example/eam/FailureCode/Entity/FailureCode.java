package com.example.eam.FailureCode.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "failure_codes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailureCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-generated ID

    @Column(name = "failure_symptom_code", nullable = false, length = 50)
    private String failureSymptomCode; // Example: NOISE, OVERHEAT

    @Column(name = "symptom_description", length = 500)
    private String symptomDescription; // Detailed description of the symptom

    @Column(name = "failure_cause_code", nullable = false, length = 50)
    private String failureCauseCode; // Example: BEARING FAILURE

    @Column(name = "cause_description", length = 500)
    private String causeDescription; // Detailed description of the cause

    @Column(name = "action_code", nullable = false, length = 50)
    private String actionCode; // Example: REPLACE PART, REPAIR

    @Column(name = "action_description", length = 500)
    private String actionDescription; // Detailed description of the action
}

