package com.example.eam.ServiceMaintenance.Service;

import com.example.eam.Asset.Entity.Asset;
import com.example.eam.Asset.Entity.AssetLocation;
import com.example.eam.Asset.Repository.AssetLocationRepository;
import com.example.eam.Asset.Repository.AssetRepository;
import com.example.eam.Enum.ServiceRequestStatus;
import com.example.eam.ServiceMaintenance.Dto.ServiceRequestCreateDto;
import com.example.eam.ServiceMaintenance.Dto.ServiceRequestResponse;
import com.example.eam.ServiceMaintenance.Dto.ServiceRequestUpdateDto;
import com.example.eam.ServiceMaintenance.Entity.ServiceMaintenance;
import com.example.eam.ServiceMaintenance.Repository.ServiceMaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ServiceMaintenanceService {

    private final ServiceMaintenanceRepository serviceRepo;
    private final AssetRepository assetRepository;
    private final AssetLocationRepository assetLocationRepository;
    

    // ---------- CREATE ----------

    @Transactional
    public ServiceRequestResponse create(ServiceRequestCreateDto dto) {

        Asset asset = null;
        if (dto.getAssetId() != null) {
            asset = assetRepository.findById(dto.getAssetId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Asset not found: " + dto.getAssetId()
                    ));
        }

        // Resolve requestId (manual or auto)
        String requestId = resolveRequestIdOnCreate(dto.getRequestId());

        String resolvedLocation = resolveLocation(dto.getLocation(), asset);

        ServiceMaintenance entity = ServiceMaintenance.builder()
                .requestId(requestId)
                .requestDate(LocalDateTime.now())
                .requesterName(dto.getRequesterName())
                .requesterContact(dto.getRequesterContact())
                .department(dto.getDepartment())
                .asset(asset)
                .location(resolvedLocation)
                .maintenanceType(dto.getMaintenanceType())
                .priority(dto.getPriority())
                .shortTitle(dto.getShortTitle())
                .problemDescription(dto.getProblemDescription())
                .preferredDate(dto.getPreferredDate())
                .preferredTime(dto.getPreferredTime())
                .safetyRisk(dto.getSafetyRisk())
                .attachmentUrl(dto.getAttachmentUrl())
                .status(ServiceRequestStatus.NEW)
                .build();

        ServiceMaintenance saved = serviceRepo.save(entity);
        return toResponse(saved);
    }

    private String resolveRequestIdOnCreate(String userProvided) {
        if (userProvided != null && !userProvided.isBlank()) {
            String trimmed = userProvided.trim();
            if (serviceRepo.existsByRequestId(trimmed)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Service Request ID already exists: " + trimmed
                );
            }
            return trimmed;
        }

        // Auto-generate: SR-000001 style using max DB id
        return generateNextRequestId();
    }

    private String generateNextRequestId() {
        String prefix = "SR-";
        long base = serviceRepo.findTopByOrderByIdDesc()
                .map(ServiceMaintenance::getId)
                .orElse(0L);

        String candidate;
        do {
            base++;
            candidate = prefix + String.format("%06d", base);
        } while (serviceRepo.existsByRequestId(candidate));

        return candidate;
    }

    // ---------- READ SINGLE ----------

    @Transactional(readOnly = true)
    public ServiceRequestResponse get(Long id) {
        ServiceMaintenance entity = getOrThrow(id);
        return toResponse(entity);
    }

    // ---------- LIST ----------

    @Transactional(readOnly = true)
    public Page<ServiceRequestResponse> list(Pageable pageable) {
        return serviceRepo.findAll(pageable)
                .map(this::toResponse);
    }

    // ---------- UPDATE (PATCH) ----------

    @Transactional
    public ServiceRequestResponse update(Long id, ServiceRequestUpdateDto dto) {
        ServiceMaintenance entity = getOrThrow(id);

        // Request ID change (optional)
        if (dto.getRequestId() != null && !dto.getRequestId().isBlank()) {
            String newReqId = dto.getRequestId().trim();
            if (!newReqId.equals(entity.getRequestId())
                    && serviceRepo.existsByRequestId(newReqId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Service Request ID already exists: " + newReqId
                );
            }
            entity.setRequestId(newReqId);
        }

        // Asset / Location
        if (dto.getAssetId() != null) {
            Asset asset = assetRepository.findById(dto.getAssetId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Asset not found: " + dto.getAssetId()
                    ));
            entity.setAsset(asset);

            if (dto.getLocation() == null || dto.getLocation().isBlank()) {
                String loc = resolveLocation(null, asset);
                if (loc != null) {
                    entity.setLocation(loc);
                }
            }
        }

        updateIfNotBlank(dto.getLocation(), entity::setLocation);
        updateIfNotBlank(dto.getRequesterName(), entity::setRequesterName);
        updateIfNotBlank(dto.getRequesterContact(), entity::setRequesterContact);
        updateIfNotBlank(dto.getDepartment(), entity::setDepartment);
        updateIfNotBlank(dto.getShortTitle(), entity::setShortTitle);
        updateIfNotBlank(dto.getProblemDescription(), entity::setProblemDescription);
        updateIfNotBlank(dto.getAttachmentUrl(), entity::setAttachmentUrl);

        updateIfNotNull(dto.getMaintenanceType(), entity::setMaintenanceType);
        updateIfNotNull(dto.getPriority(), entity::setPriority);
        updateIfNotNull(dto.getPreferredDate(), entity::setPreferredDate);
        updateIfNotNull(dto.getPreferredTime(), entity::setPreferredTime);
        updateIfNotNull(dto.getSafetyRisk(), entity::setSafetyRisk);
        updateIfNotNull(dto.getStatus(), entity::setStatus);

        ServiceMaintenance saved = serviceRepo.save(entity);
        return toResponse(saved);
    }

    // ---------- DELETE ----------

    @Transactional
    public void delete(Long id) {
        ServiceMaintenance entity = getOrThrow(id);
        serviceRepo.delete(entity);
    }

    // ---------- Helpers ----------

    private ServiceMaintenance getOrThrow(Long id) {
        return serviceRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Service request not found: " + id
                ));
    }

    private String resolveLocation(String requestLocation, Asset asset) {
        if (requestLocation != null && !requestLocation.isBlank()) {
            return requestLocation.trim();
        }
        if (asset == null) return null;

        return assetLocationRepository.findByAsset_Id(asset.getId())
                .map(AssetLocation::getPrimaryLocation)
                .orElse(null);
    }

    private <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private void updateIfNotBlank(String value, Consumer<String> setter) {
        if (value != null && !value.trim().isEmpty()) {
            setter.accept(value.trim());
        }
    }

    private ServiceRequestResponse toResponse(ServiceMaintenance entity) {
        Asset asset = entity.getAsset();

        return ServiceRequestResponse.builder()
                .id(entity.getId())
                .requestId(entity.getRequestId())
                .requestDate(entity.getRequestDate())
                .requesterName(entity.getRequesterName())
                .requesterContact(entity.getRequesterContact())
                .department(entity.getDepartment())
                .assetDbId(asset != null ? asset.getId() : null)
                .assetId(asset != null ? asset.getAssetId() : null)
                .assetName(asset != null ? asset.getAssetName() : null)
                .location(entity.getLocation())
                .maintenanceType(entity.getMaintenanceType())
                .priority(entity.getPriority())
                .shortTitle(entity.getShortTitle())
                .problemDescription(entity.getProblemDescription())
                .preferredDate(entity.getPreferredDate())
                .preferredTime(entity.getPreferredTime())
                .safetyRisk(entity.getSafetyRisk())
                .attachmentUrl(entity.getAttachmentUrl())
                .status(entity.getStatus())
                .build();
    }
}

