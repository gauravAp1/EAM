package com.example.eam.WorkOrder.Dto;

import com.example.eam.Enum.PriorityLevel;
import com.example.eam.Enum.WorkOrderSource;
import com.example.eam.Enum.WorkOrderStatus;
import com.example.eam.Enum.WorkType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WorkOrderCreateRequest {

    // Asset optional (location-only WO allowed)
    private Long assetId;

    // If assetId is null, location is required
    private String location;

    @NotNull
    private WorkType workType;

    @NotNull
    private PriorityLevel priority;

    @NotBlank
    private String woTitle;

    private String descriptionScope;

    private String planner;
    private String assignedTechnician;
    private String assignedCrewTeam;

    private LocalDateTime plannedStartDateTime;
    private LocalDateTime plannedEndDateTime;
    private LocalDate targetCompletionDate;

    // optional; defaults handled in service
    private WorkOrderStatus status;
    private WorkOrderSource source;
}

