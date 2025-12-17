package com.example.eam.WorkOrder.Dto;

import com.example.eam.Enum.PriorityLevel;
import com.example.eam.Enum.WorkOrderSource;
import com.example.eam.Enum.WorkOrderStatus;
import com.example.eam.Enum.WorkType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private Long assignedTechnicianId;
    private Long assignedTeamId;

    private LocalDateTime plannedStartDateTime;
    private LocalDateTime plannedEndDateTime;
    private LocalDate targetCompletionDate;

    private BigDecimal estimatedLaborHours;
    private BigDecimal estimatedMaterialCost;
    private BigDecimal estimatedTotalCost;

    // optional; defaults handled in service
    private WorkOrderStatus status;
    private WorkOrderSource source;

    private List<WorkOrderMaterialPlanRequest> plannedMaterials;
}
