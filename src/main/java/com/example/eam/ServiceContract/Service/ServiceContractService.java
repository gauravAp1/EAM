package com.example.eam.ServiceContract.Service;

import com.example.eam.Asset.Entity.Asset;
import com.example.eam.Asset.Repository.AssetRepository;
import com.example.eam.ServiceContract.Dto.*;
import com.example.eam.ServiceContract.Entity.ServiceContract;
import com.example.eam.ServiceContract.Repository.ServiceContractRepository;
import com.example.eam.VendorManagement.Entity.Vendor; // ✅ change package if needed
import com.example.eam.VendorManagement.Repository.VendorRepository; // ✅ change package if needed
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceContractService {

    private final ServiceContractRepository repository;
    private final VendorRepository vendorRepository;  // adjust package if needed
    private final AssetRepository assetRepository;

    @Transactional
    public ServiceContractDetailsResponse create(ServiceContractCreateRequest req) {

        Vendor vendor = vendorRepository.findById(req.getVendorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor not found: " + req.getVendorId()));

        validateDates(req.getStartDate(), req.getEndDate());
        validateUptime(req.getUptimeSlaPercent());

        ServiceContract sc = ServiceContract.builder()
                .contractId(generateNextContractId())
                .contractName(req.getContractName().trim())
                .vendor(vendor)
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .coverageType(req.getCoverageType())
                .responseTimeSlaValue(req.getResponseTimeSlaValue())
                .responseTimeSlaUnit(req.getResponseTimeSlaUnit())
                .uptimeSlaPercent(scaleUptime(req.getUptimeSlaPercent()))
                .notes(trimOrNull(req.getNotes()))
                .deleted(false)
                .build();

        if (req.getCoveredAssetDbIds() != null) {
            sc.setCoveredAssets(loadAssets(req.getCoveredAssetDbIds()));
        }

        ServiceContract saved = repository.save(sc);
        return toDetails(saved);
    }

    @Transactional
    public ServiceContractDetailsResponse patch(Long id, ServiceContractPatchRequest req) {
        ServiceContract sc = getOrThrow(id);

        if (req.getVendorId() != null) {
            Vendor vendor = vendorRepository.findById(req.getVendorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor not found: " + req.getVendorId()));
            sc.setVendor(vendor);
        }

        updateIfNotBlank(req.getContractName(), sc::setContractName);

        // dates
        LocalDate start = req.getStartDate() != null ? req.getStartDate() : sc.getStartDate();
        LocalDate end = req.getEndDate() != null ? req.getEndDate() : sc.getEndDate();
        if (req.getStartDate() != null || req.getEndDate() != null) {
            validateDates(start, end);
            sc.setStartDate(start);
            sc.setEndDate(end);
        }

        if (req.getCoverageType() != null) sc.setCoverageType(req.getCoverageType());
        if (req.getResponseTimeSlaValue() != null) sc.setResponseTimeSlaValue(req.getResponseTimeSlaValue());
        if (req.getResponseTimeSlaUnit() != null) sc.setResponseTimeSlaUnit(req.getResponseTimeSlaUnit());

        if (req.getUptimeSlaPercent() != null) {
            validateUptime(req.getUptimeSlaPercent());
            sc.setUptimeSlaPercent(scaleUptime(req.getUptimeSlaPercent()));
        }

        if (req.getNotes() != null) sc.setNotes(trimOrNull(req.getNotes()));

        // replace covered assets if provided
        if (req.getCoveredAssetDbIds() != null) {
            sc.getCoveredAssets().clear();
            sc.getCoveredAssets().addAll(loadAssets(req.getCoveredAssetDbIds()));
        }

        ServiceContract saved = repository.save(sc);
        return toDetails(saved);
    }

    @Transactional(readOnly = true)
    public ServiceContractDetailsResponse get(Long id) {
        return toDetails(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<ServiceContractDetailsResponse> list(Pageable pageable) {
        return repository.findByDeletedFalse(pageable).map(this::toDetails);
    }

    @Transactional
    public void delete(Long id) {
        ServiceContract sc = getOrThrow(id);
        sc.setDeleted(true);
        repository.save(sc);
    }

    // ---------------- Helpers ----------------

    private ServiceContract getOrThrow(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service contract not found: " + id));
    }

    private Set<Asset> loadAssets(Set<Long> assetIds) {
        if (assetIds.isEmpty()) return new LinkedHashSet<>();
        var assets = assetRepository.findAllById(assetIds);
        if (assets.size() != assetIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more assets not found in coveredAssetDbIds");
        }
        return new LinkedHashSet<>(assets);
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate and endDate are required");
        }
        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate cannot be before startDate");
        }
    }

    private void validateUptime(BigDecimal uptime) {
        if (uptime == null) return;
        if (uptime.compareTo(BigDecimal.ZERO) < 0 || uptime.compareTo(new BigDecimal("100")) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "uptimeSlaPercent must be between 0 and 100");
        }
    }

    private BigDecimal scaleUptime(BigDecimal uptime) {
        if (uptime == null) return null;
        return uptime.setScale(2, RoundingMode.HALF_UP);
    }

    private String generateNextContractId() {
        String prefix = "SC-";
        long base = repository.findTopByOrderByIdDesc()
                .map(ServiceContract::getId)
                .orElse(0L);

        String candidate;
        do {
            base++;
            candidate = prefix + String.format("%06d", base);
        } while (repository.existsByContractId(candidate));

        return candidate;
    }

    private <T> void updateIfNotNull(T v, Consumer<T> setter) {
        if (v != null) setter.accept(v);
    }

    private void updateIfNotBlank(String v, Consumer<String> setter) {
        if (v != null && !v.trim().isEmpty()) setter.accept(v.trim());
    }

    private String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private ServiceContractDetailsResponse toDetails(ServiceContract sc) {
        Vendor v = sc.getVendor();

        Set<AssetMiniResponse> assets = sc.getCoveredAssets() == null ? Set.of() :
                sc.getCoveredAssets().stream()
                        .map(a -> AssetMiniResponse.builder()
                                .assetDbId(a.getId())
                                .assetId(a.getAssetId())
                                .assetName(a.getAssetName())
                                .build())
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        return ServiceContractDetailsResponse.builder()
                .id(sc.getId())
                .contractId(sc.getContractId())
                .contractName(sc.getContractName())
                .vendorDbId(v != null ? v.getId() : null)
                .vendorId(null) // if you have business vendorId, map it here
                .vendorName(v != null ? v.getVendorName() : null)
                .startDate(sc.getStartDate())
                .endDate(sc.getEndDate())
                .coverageType(sc.getCoverageType())
                .responseTimeSlaValue(sc.getResponseTimeSlaValue())
                .responseTimeSlaUnit(sc.getResponseTimeSlaUnit())
                .uptimeSlaPercent(sc.getUptimeSlaPercent())
                .notes(sc.getNotes())
                .coveredAssets(assets)
                .createdAt(sc.getCreatedAt())
                .updatedAt(sc.getUpdatedAt())
                .build();
    }
}

