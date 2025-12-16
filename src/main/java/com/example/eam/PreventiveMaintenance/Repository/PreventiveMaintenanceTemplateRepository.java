package com.example.eam.PreventiveMaintenance.Repository;

import com.example.eam.PreventiveMaintenance.Entity.PreventiveMaintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreventiveMaintenanceTemplateRepository extends JpaRepository<PreventiveMaintenance, Long> {
    boolean existsByPmId(String pmId);
    Page<PreventiveMaintenance> findByDeletedFalse(Pageable pageable);
}

