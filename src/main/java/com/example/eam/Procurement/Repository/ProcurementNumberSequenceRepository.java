package com.example.eam.Procurement.Repository;

import com.example.eam.Procurement.Entity.ProcurementNumberSequence;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface ProcurementNumberSequenceRepository extends JpaRepository<ProcurementNumberSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000"))
    Optional<ProcurementNumberSequence> findBySequenceKeyAndYear(String sequenceKey, Integer year);
}
