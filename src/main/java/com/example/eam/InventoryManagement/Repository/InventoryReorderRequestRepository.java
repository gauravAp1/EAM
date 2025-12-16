package com.example.eam.InventoryManagement.Repository;

import com.example.eam.InventoryManagement.Entity.InventoryReorderRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryReorderRequestRepository extends JpaRepository<InventoryReorderRequest, Long> {
    boolean existsByReorderId(String reorderId);
}

