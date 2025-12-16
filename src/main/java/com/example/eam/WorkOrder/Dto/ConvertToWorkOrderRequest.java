package com.example.eam.WorkOrder.Dto;


import com.example.eam.Enum.PriorityLevel;
import com.example.eam.Enum.WorkOrderStatus;
import com.example.eam.Enum.WorkType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ConvertToWorkOrderRequest {

    // Optional overrides; if null we copy from Service Request
    private WorkType workType;
    private PriorityLevel priority;
    private String woTitle;
    private String descriptionScope;

    private String planner;
    private String assignedTechnician;
    private String assignedCrewTeam;

    private LocalDateTime plannedStartDateTime;
    private LocalDateTime plannedEndDateTime;
    private LocalDate targetCompletionDate;

    // default is DRAFT
    private WorkOrderStatus status;
}

