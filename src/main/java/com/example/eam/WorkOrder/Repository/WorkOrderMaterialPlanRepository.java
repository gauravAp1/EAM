package com.example.eam.WorkOrder.Repository;

import com.example.eam.WorkOrder.Entity.WorkOrder;
import com.example.eam.WorkOrder.Entity.WorkOrderMaterialPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderMaterialPlanRepository extends JpaRepository<WorkOrderMaterialPlan, Long> {

    List<WorkOrderMaterialPlan> findByWorkOrder_Id(Long workOrderId);

    void deleteByWorkOrder(WorkOrder workOrder);
}
