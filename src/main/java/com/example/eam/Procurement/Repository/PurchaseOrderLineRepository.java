package com.example.eam.Procurement.Repository;

import com.example.eam.Procurement.Entity.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine, Long> {

    List<PurchaseOrderLine> findByPoId(Long poId);

    Optional<PurchaseOrderLine> findByIdAndPoId(Long id, Long poId);
}
