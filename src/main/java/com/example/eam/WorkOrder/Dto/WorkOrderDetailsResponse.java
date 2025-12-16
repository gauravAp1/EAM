package com.example.eam.WorkOrder.Dto;


import com.example.eam.Enum.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "workOrderId",
        "linkedServiceRequestDbId",
        "linkedServiceRequestId",
        "assetDbId",
        "assetId",
        "assetName",
        "location",
        "workType",
        "priority",
        "woTitle",
        "descriptionScope",
        "planner",
        "assignedTechnician",
        "assignedCrewTeam",
        "plannedStartDateTime",
        "plannedEndDateTime",
        "targetCompletionDate",
        "status",
        "source",
        "createdAt",
        "updatedAt"
})
public class WorkOrderDetailsResponse {

    private Long id;
    private String workOrderId;

    private Long linkedServiceRequestDbId;   // ServiceMaintenance.id
    private String linkedServiceRequestId;   // ServiceMaintenance.serviceRequestId (business id)

    private Long assetDbId;                  // Asset.id
    private String assetId;                  // Asset.assetId (business id)
    private String assetName;

    private String location;

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

    private WorkOrderStatus status;
    private WorkOrderSource source;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

