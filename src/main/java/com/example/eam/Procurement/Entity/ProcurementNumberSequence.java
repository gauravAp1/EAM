package com.example.eam.Procurement.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "procurement_number_sequences",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_proc_seq_key_year", columnNames = {"sequence_key", "sequence_year"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcurementNumberSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sequence_key", nullable = false, length = 32)
    private String sequenceKey;

    @Column(name = "sequence_year", nullable = false)
    private Integer year;

    @Column(name = "last_value", nullable = false)
    private Long lastValue;
}
