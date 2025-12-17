package com.example.eam.WorkOrder.Repository;

import com.example.eam.WorkOrder.Entity.WorkOrder;
import com.example.eam.WorkOrder.Entity.WorkOrderMaterialUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderMaterialUsageRepository extends JpaRepository<WorkOrderMaterialUsage, Long> {

    List<WorkOrderMaterialUsage> findByWorkOrder_Id(Long workOrderId);

    void deleteByWorkOrder(WorkOrder workOrder);
}
