package com.example.eam.WorkOrder.Repository;

import com.example.eam.WorkOrder.Entity.WorkOrder;
import com.example.eam.WorkOrder.Entity.WorkOrderLaborEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderLaborEntryRepository extends JpaRepository<WorkOrderLaborEntry, Long> {

    List<WorkOrderLaborEntry> findByWorkOrder_Id(Long workOrderId);

    void deleteByWorkOrder(WorkOrder workOrder);
}
