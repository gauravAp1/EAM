package com.example.eam.Dashboard.Dto;

import com.example.eam.Enum.PriorityLevel;
import com.example.eam.Enum.WorkOrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RecentWorkOrderDto {

    private String workOrderId;
    private String title;
    private String asset;
    private String technician;
    private LocalDate dueDate;
    private PriorityLevel priority;
    private WorkOrderStatus status;
}
