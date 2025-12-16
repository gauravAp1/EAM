package com.example.eam.Procurement.Repository;

import com.example.eam.Procurement.Entity.PurchaseRequisition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseRequisitionRepository extends JpaRepository<PurchaseRequisition, Long> {

    boolean existsByPrId(String prId);

    Optional<PurchaseRequisition> findTopByOrderByIdDesc();

    Optional<PurchaseRequisition> findByIdAndDeletedFalse(Long id);

    Page<PurchaseRequisition> findByDeletedFalse(Pageable pageable);
}

