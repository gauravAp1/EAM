package com.example.eam.WorkOrder.Repository;


import com.example.eam.WorkOrder.Entity.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    
    boolean existsByPmTemplate_IdAndPmDueDate(Long pmTemplateId, LocalDate pmDueDate);

    boolean existsByWorkOrderId(String workOrderId);

    Optional<WorkOrder> findByIdAndDeletedFalse(Long id);

    Page<WorkOrder> findByDeletedFalse(Pageable pageable);

    Optional<WorkOrder> findByLinkedRequest_Id(Long serviceRequestPkId);
}

