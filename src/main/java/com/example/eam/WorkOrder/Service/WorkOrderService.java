package com.example.eam.WorkOrder.Service;


import com.example.eam.Asset.Entity.Asset;
import com.example.eam.Asset.Entity.AssetLocation;
import com.example.eam.Asset.Repository.AssetLocationRepository;
import com.example.eam.Asset.Repository.AssetRepository;
import com.example.eam.Enum.*;
import com.example.eam.ServiceMaintenance.Entity.ServiceMaintenance;
import com.example.eam.ServiceMaintenance.Repository.ServiceMaintenanceRepository;
import com.example.eam.WorkOrder.Dto.*;
import com.example.eam.WorkOrder.Entity.WorkOrder;
import com.example.eam.WorkOrder.Repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final AssetRepository assetRepository;
    private final AssetLocationRepository assetLocationRepository;
    private final ServiceMaintenanceRepository serviceMaintenanceRepository;

    // ---------------- CREATE (Manual) ----------------

    @Transactional
    public WorkOrderDetailsResponse createWorkOrder(WorkOrderCreateRequest request) {

        Asset asset = null;
        if (request.getAssetId() != null) {
            asset = assetRepository.findById(request.getAssetId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found"));
        }

        String location = resolveLocation(asset, request.getLocation());

        // if asset is not selected, location must be provided
        if (asset == null && isBlank(location)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location is required when Asset is not selected");
        }

        WorkOrder wo = WorkOrder.builder()
                .workOrderId(generateUniqueWorkOrderId())
                .linkedRequest(null)
                .asset(asset)
                .location(location)
                .workType(request.getWorkType())
                .priority(request.getPriority())
                .woTitle(request.getWoTitle())
                .descriptionScope(request.getDescriptionScope())
                .planner(request.getPlanner())
                .assignedTechnician(request.getAssignedTechnician())
                .assignedCrewTeam(request.getAssignedCrewTeam())
                .plannedStartDateTime(request.getPlannedStartDateTime())
                .plannedEndDateTime(request.getPlannedEndDateTime())
                .targetCompletionDate(request.getTargetCompletionDate())
                .status(request.getStatus() != null ? request.getStatus() : WorkOrderStatus.DRAFT)
                .source(request.getSource() != null ? request.getSource() : WorkOrderSource.MANUAL)
                .deleted(false)
                .build();

        WorkOrder saved = workOrderRepository.save(wo);
        return toDetailsResponse(saved);
    }

    // ---------------- CONVERT SR -> WO ----------------

    @Transactional
    public WorkOrderDetailsResponse convertServiceRequestToWorkOrder(Long serviceRequestDbId,
                                                                     ConvertToWorkOrderRequest overrides) {

        ServiceMaintenance sr = serviceMaintenanceRepository.findById(serviceRequestDbId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Request not found"));

        // Already converted?
        if (sr.getStatus() == ServiceRequestStatus.CONVERTED_TO_WO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Service Request is already converted to Work Order");
        }
        workOrderRepository.findByLinkedRequest_Id(serviceRequestDbId).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Work Order already exists for this Service Request");
        });

        Asset asset = sr.getAsset(); // may be null
        String location = resolveLocation(asset, sr.getLocation());

        if (asset == null && isBlank(location)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot convert to WO: both Asset and Location are missing");
        }

        // Copy from SR, allow overrides
        WorkType workType = (overrides != null && overrides.getWorkType() != null)
                ? overrides.getWorkType()
                : mapMaintenanceTypeToWorkType(sr.getMaintenanceType());
    PriorityLevel priority = (overrides != null && overrides.getPriority() != null)
        ? overrides.getPriority()
        : mapRequestPriorityToPriorityLevel(sr.getPriority());


        String woTitle = (overrides != null && !isBlank(overrides.getWoTitle()))
                ? overrides.getWoTitle()
                : sr.getShortTitle();

        String desc = (overrides != null && !isBlank(overrides.getDescriptionScope()))
                ? overrides.getDescriptionScope()
                : sr.getProblemDescription();

        WorkOrderStatus status = (overrides != null && overrides.getStatus() != null)
                ? overrides.getStatus()
                : WorkOrderStatus.DRAFT;

        WorkOrder wo = WorkOrder.builder()
                .workOrderId(generateUniqueWorkOrderId())
                .linkedRequest(sr)
                .asset(asset)
                .location(location)
                .workType(workType != null ? workType : WorkType.CORRECTIVE)
                .priority(priority != null ? priority : PriorityLevel.MEDIUM)
                .woTitle(!isBlank(woTitle) ? woTitle : "Work Order from Service Request")
                .descriptionScope(desc)
                .planner(overrides != null ? overrides.getPlanner() : null)
                .assignedTechnician(overrides != null ? overrides.getAssignedTechnician() : null)
                .assignedCrewTeam(overrides != null ? overrides.getAssignedCrewTeam() : null)
                .plannedStartDateTime(overrides != null ? overrides.getPlannedStartDateTime() : null)
                .plannedEndDateTime(overrides != null ? overrides.getPlannedEndDateTime() : null)
                .targetCompletionDate(overrides != null ? overrides.getTargetCompletionDate() : null)
                .status(status)
                .source(WorkOrderSource.REQUEST)
                .deleted(false)
                .build();

        WorkOrder saved = workOrderRepository.save(wo);

        // Update SR status + store WO code (nice for UI)
        sr.setStatus(ServiceRequestStatus.CONVERTED_TO_WO);
        sr.setLinkedWorkOrderId(saved.getWorkOrderId());
        serviceMaintenanceRepository.save(sr);

        return toDetailsResponse(saved);
    }

    // ---------------- PATCH UPDATE ----------------

    @Transactional
    public WorkOrderDetailsResponse patchWorkOrder(Long id, WorkOrderPatchRequest request) {
        WorkOrder wo = getWorkOrderOrThrow(id);

        // asset update
        if (request.getAssetId() != null) {
            Asset asset = assetRepository.findById(request.getAssetId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found"));
            wo.setAsset(asset);

            // if caller did not send location, recalc location from asset
            if (isBlank(request.getLocation())) {
                String autoLoc = resolveLocation(asset, null);
                if (!isBlank(autoLoc)) wo.setLocation(autoLoc);
            }
        }

        updateIfNotNull(request.getLocation(), wo::setLocation);
        if (request.getWorkType() != null) wo.setWorkType(request.getWorkType());
        if (request.getPriority() != null) wo.setPriority(request.getPriority());

        updateIfNotNull(request.getWoTitle(), wo::setWoTitle);
        updateIfNotNull(request.getDescriptionScope(), wo::setDescriptionScope);
        updateIfNotNull(request.getPlanner(), wo::setPlanner);
        updateIfNotNull(request.getAssignedTechnician(), wo::setAssignedTechnician);
        updateIfNotNull(request.getAssignedCrewTeam(), wo::setAssignedCrewTeam);
        updateIfNotNull(request.getPlannedStartDateTime(), wo::setPlannedStartDateTime);
        updateIfNotNull(request.getPlannedEndDateTime(), wo::setPlannedEndDateTime);
        updateIfNotNull(request.getTargetCompletionDate(), wo::setTargetCompletionDate);

        if (request.getStatus() != null) wo.setStatus(request.getStatus());
        if (request.getSource() != null) wo.setSource(request.getSource());

        // validation
        if (wo.getAsset() == null && isBlank(wo.getLocation())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Location is required when Asset is not selected");
        }

        WorkOrder saved = workOrderRepository.save(wo);
        return toDetailsResponse(saved);
    }

    // ---------------- READ ----------------

    @Transactional(readOnly = true)
    public WorkOrderDetailsResponse getWorkOrderDetails(Long id) {
        WorkOrder wo = getWorkOrderOrThrow(id);
        return toDetailsResponse(wo);
    }

    @Transactional(readOnly = true)
    public WorkOrderListResponse listWorkOrders(Pageable pageable) {
        Page<WorkOrder> page = workOrderRepository.findByDeletedFalse(pageable);
        List<WorkOrderDetailsResponse> rows = page.getContent().stream()
                .map(this::toDetailsResponse)
                .toList();

        return WorkOrderListResponse.builder()
                .workOrders(rows)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    // ---------------- DELETE (soft delete) ----------------

    @Transactional
    public void deleteWorkOrder(Long id) {
        WorkOrder wo = getWorkOrderOrThrow(id);
        wo.setDeleted(true);
        wo.setStatus(WorkOrderStatus.CANCELED);
        workOrderRepository.save(wo);
    }

    // ---------------- Helpers ----------------

    private WorkOrder getWorkOrderOrThrow(Long id) {
        return workOrderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Work Order not found"));
    }

    private <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    private String resolveLocation(Asset asset, String providedLocation) {
        if (!isBlank(providedLocation)) return providedLocation.trim();
        if (asset == null) return null;

        return assetLocationRepository.findByAsset_Id(asset.getId())
                .map(AssetLocation::getPrimaryLocation)
                .filter(v -> !isBlank(v))
                .orElse(null);
    }

    private String generateUniqueWorkOrderId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD

        for (int attempt = 0; attempt < 30; attempt++) {
            int rand = ThreadLocalRandom.current().nextInt(0, 10000);
            String candidate = String.format("WO-%s-%04d", datePart, rand);

            if (!workOrderRepository.existsByWorkOrderId(candidate)) {
                return candidate;
            }
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to generate unique Work Order ID");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private WorkType mapMaintenanceTypeToWorkType(MaintenanceType mt) {
        if (mt == null) return WorkType.CORRECTIVE;
        return switch (mt) {
            case CORRECTIVE -> WorkType.CORRECTIVE;
            case PREVENTIVE -> WorkType.PREVENTIVE;
            case PREDICTIVE -> WorkType.PREDICTIVE;
            case EMERGENCY -> WorkType.EMERGENCY;
        };
    }

    private WorkOrderDetailsResponse toDetailsResponse(WorkOrder wo) {
        Asset asset = wo.getAsset();
        ServiceMaintenance sr = wo.getLinkedRequest();

        return WorkOrderDetailsResponse.builder()
                .id(wo.getId())
                .workOrderId(wo.getWorkOrderId())
                .linkedServiceRequestDbId(sr != null ? sr.getId() : null)
                .linkedServiceRequestId(sr != null ? sr.getRequestId() : null)
                .assetDbId(asset != null ? asset.getId() : null)
                .assetId(asset != null ? asset.getAssetId() : null)
                .assetName(asset != null ? asset.getAssetName() : null)
                .location(wo.getLocation())
                .workType(wo.getWorkType())
                .priority(wo.getPriority())
                .woTitle(wo.getWoTitle())
                .descriptionScope(wo.getDescriptionScope())
                .planner(wo.getPlanner())
                .assignedTechnician(wo.getAssignedTechnician())
                .assignedCrewTeam(wo.getAssignedCrewTeam())
                .plannedStartDateTime(wo.getPlannedStartDateTime())
                .plannedEndDateTime(wo.getPlannedEndDateTime())
                .targetCompletionDate(wo.getTargetCompletionDate())
                .status(wo.getStatus())
                .source(wo.getSource())
                .createdAt(wo.getCreatedAt())
                .updatedAt(wo.getUpdatedAt())
                .build();
    }
    private PriorityLevel mapRequestPriorityToPriorityLevel(RequestPriority p) {
    if (p == null) return null;

    return switch (p) {
        case LOW -> PriorityLevel.LOW;
        case MEDIUM -> PriorityLevel.MEDIUM;
        case HIGH -> PriorityLevel.HIGH;
        case CRITICAL -> PriorityLevel.CRITICAL;
    };
}

}

