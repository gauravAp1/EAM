package com.example.eam.Procurement.Repository;

import com.example.eam.Procurement.Entity.MaterialRequisition;
import com.example.eam.Procurement.Enum.MaterialRequisitionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialRequisitionRepository extends JpaRepository<MaterialRequisition, Long> {

    boolean existsByMrNumber(String mrNumber);

    Optional<MaterialRequisition> findByMrNumber(String mrNumber);

    Page<MaterialRequisition> findByStatus(MaterialRequisitionStatus status, Pageable pageable);
}
