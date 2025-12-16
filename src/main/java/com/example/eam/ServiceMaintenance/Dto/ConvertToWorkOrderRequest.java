package com.example.eam.ServiceMaintenance.Dto;

import com.example.eam.Enum.PriorityLevel;
import com.example.eam.Enum.WorkOrderStatus;
import com.example.eam.Enum.WorkType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ConvertToWorkOrderRequest {
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

    private WorkOrderStatus status; // default handled in service if null
}

