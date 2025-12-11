package com.example.eam.ServiceMaintenance.Repository;

import com.example.eam.Enum.ServiceRequestStatus;
import com.example.eam.ServiceMaintenance.Entity.ServiceMaintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceMaintenanceRepository extends JpaRepository<ServiceMaintenance, Long> {

    Page<ServiceMaintenance> findByStatus(ServiceRequestStatus status, Pageable pageable);

    Page<ServiceMaintenance> findByAsset_Id(Long assetId, Pageable pageable);

    boolean existsByRequestId(String requestId);

    Optional<ServiceMaintenance> findTopByOrderByIdDesc();   // for auto-numbering
}

