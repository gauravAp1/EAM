package com.example.eam.Procurement.Repository;

import com.example.eam.Procurement.Entity.MaterialRequisitionLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialRequisitionLineRepository extends JpaRepository<MaterialRequisitionLine, Long> {

    List<MaterialRequisitionLine> findByMaterialRequisitionId(Long materialRequisitionId);
}
