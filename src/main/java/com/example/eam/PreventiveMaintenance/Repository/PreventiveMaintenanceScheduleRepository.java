package com.example.eam.PreventiveMaintenance.Repository;

import com.example.eam.PreventiveMaintenance.Entity.PreventiveMaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreventiveMaintenanceScheduleRepository extends JpaRepository<PreventiveMaintenanceSchedule, Long> {
    Optional<PreventiveMaintenanceSchedule> findByTemplate_Id(Long templateId);
}

