package com.example.eam.Procurement.Service;

import com.example.eam.Asset.Entity.Asset;
import com.example.eam.Asset.Repository.AssetRepository;
import com.example.eam.Enum.PriorityLevel;
import com.example.eam.Enum.PrStatus;
import com.example.eam.Enum.PrLineType;
import com.example.eam.InventoryManagement.Entity.InventoryItem;
import com.example.eam.InventoryManagement.Repository.InventoryItemRepository;
import com.example.eam.Procurement.Dto.*;
import com.example.eam.Procurement.Entity.PurchaseRequisition;
import com.example.eam.Procurement.Entity.PurchaseRequisitionLine;
import com.example.eam.Procurement.Repository.PurchaseRequisitionRepository;
import com.example.eam.VendorManagement.Entity.Vendor;
import com.example.eam.VendorManagement.Repository.VendorRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PurchaseRequisitionService {

    private final PurchaseRequisitionRepository prRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final AssetRepository assetRepository;
    private final VendorRepository vendorRepository;

    // ---------------- CREATE ----------------

    @Transactional
    public PrDetailsResponse create(PrCreateRequest request) {

        requireNonBlank(request.getRequester(), "requester is required");
        if (request.getRequiredByDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "requiredByDate is required");
        }
        if (request.getLines() == null || request.getLines().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least 1 PR line is required");
        }

        validatePriority(request.getPriority());

        Vendor preferredVendor = null;
        if (request.getPreferredVendorId() != null) {
            preferredVendor = vendorRepository.findById(request.getPreferredVendorId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Preferred vendor not found: " + request.getPreferredVendorId()
                    ));
        }

        PurchaseRequisition pr = PurchaseRequisition.builder()
                .prId(generateUniquePrId())
                .requester(request.getRequester().trim())
                .requestDate(LocalDate.now())
                .requiredByDate(request.getRequiredByDate())
                .priority(request.getPriority())
                .department(trimOrNull(request.getDepartment()))
                .costCenter(trimOrNull(request.getCostCenter()))
                .currency(trimOrNull(request.getCurrency()))
                .notes(trimOrNull(request.getNotes()))
                .preferredVendor(preferredVendor)
                .requiredForType(request.getRequiredForType())
                .requiredForReference(trimOrNull(request.getRequiredForReference()))
                .status(PrStatus.DRAFT)
                .deleted(false)
                .build();

        // build lines
        for (PrLineCreateDto l : request.getLines()) {
            pr.addLine(buildLine(pr, l));
        }

        pr.setTotalEstimatedCost(sumTotal(pr.getLines()));

        PurchaseRequisition saved = prRepository.save(pr);
        return toDetails(saved);
    }

    // ---------------- PATCH UPDATE ----------------

    @Transactional
    public PrDetailsResponse patch(Long id, PrPatchRequest request) {
        PurchaseRequisition pr = getOrThrow(id);

        // Realistic: once not DRAFT, prevent editing lines
        if (request.getLines() != null && pr.getStatus() != PrStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "PR lines cannot be edited unless status is DRAFT");
        }

        if (request.getPriority() != null) validatePriority(request.getPriority());

        updateIfNotBlank(request.getRequester(), pr::setRequester);
        updateIfNotNull(request.getRequiredByDate(), pr::setRequiredByDate);
        updateIfNotNull(request.getPriority(), pr::setPriority);
        updateIfNotNull(request.getRequiredForType(), pr::setRequiredForType);
        updateIfNotNull(trimOrNull(request.getRequiredForReference()), pr::setRequiredForReference);

        updateIfNotNull(trimOrNull(request.getDepartment()), pr::setDepartment);
        updateIfNotNull(trimOrNull(request.getCostCenter()), pr::setCostCenter);
        updateIfNotNull(trimOrNull(request.getCurrency()), pr::setCurrency);
        updateIfNotNull(trimOrNull(request.getNotes()), pr::setNotes);

        if (request.getPreferredVendorId() != null) {
            Vendor vendor = vendorRepository.findById(request.getPreferredVendorId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Preferred vendor not found: " + request.getPreferredVendorId()
                    ));
            pr.setPreferredVendor(vendor);
        }

        if (request.getStatus() != null) {
            pr.setStatus(request.getStatus());
        }

        // Replace lines if provided (allowed only in DRAFT by above check)
        if (request.getLines() != null) {
            pr.getLines().clear();
            for (PrLineCreateDto l : request.getLines()) {
                pr.addLine(buildLine(pr, l));
            }
        }

        pr.setTotalEstimatedCost(sumTotal(pr.getLines()));

        PurchaseRequisition saved = prRepository.save(pr);
        return toDetails(saved);
    }

    // ---------------- READ ----------------

    @Transactional(readOnly = true)
    public PrDetailsResponse get(Long id) {
        return toDetails(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<PrDetailsResponse> list(Pageable pageable) {
        return prRepository.findByDeletedFalse(pageable).map(this::toDetails);
    }

    // ---------------- DELETE (soft) ----------------

    @Transactional
    public void delete(Long id) {
        PurchaseRequisition pr = getOrThrow(id);

        // realistic safety
        if (pr.getStatus() == PrStatus.CONVERTED_TO_PO || pr.getStatus() == PrStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete PR in status: " + pr.getStatus());
        }

        pr.setDeleted(true);
        prRepository.save(pr);
    }

    // ---------------- Helpers ----------------

    private PurchaseRequisition getOrThrow(Long id) {
        return prRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Purchase Requisition not found"
                ));
    }

    private void validatePriority(PriorityLevel p) {
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "priority is required");
        }
        if (p == PriorityLevel.CRITICAL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PR priority cannot be CRITICAL");
        }
    }

    private PurchaseRequisitionLine buildLine(PurchaseRequisition pr, PrLineCreateDto dto) {

        if (dto.getLineType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lineType is required for each PR line");
        }
        if (dto.getQtyRequested() == null || dto.getQtyRequested() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "qtyRequested must be > 0");
        }

        InventoryItem item = null;
        Asset asset = null;

        // Validate per type
        if (dto.getLineType() == PrLineType.PART) {
            if (dto.getInventoryItemDbId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "inventoryItemDbId is required when lineType=PART");
            }
            item = inventoryItemRepository.findById(dto.getInventoryItemDbId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Inventory item not found: " + dto.getInventoryItemDbId()
                    ));
        }

        if (dto.getLineType() == PrLineType.ASSET) {
            if (dto.getAssetDbId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "assetDbId is required when lineType=ASSET");
            }
            asset = assetRepository.findById(dto.getAssetDbId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Asset not found: " + dto.getAssetDbId()
                    ));
        }

        if (dto.getLineType() == PrLineType.SERVICE) {
            if (dto.getDescription() == null || dto.getDescription().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "description is required when lineType=SERVICE");
            }
        }

        // pricing
        BigDecimal unitPrice = dto.getEstimatedUnitPrice();
        if (unitPrice == null) {
            // optional default for PART: take from item if you store it
            // unitPrice = (item != null && item.getCostPerUnit() != null) ? item.getCostPerUnit() : BigDecimal.ZERO;
            unitPrice = BigDecimal.ZERO;
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "estimatedUnitPrice cannot be negative");
        }

        BigDecimal lineTotal = unitPrice
                .multiply(BigDecimal.valueOf(dto.getQtyRequested()))
                .setScale(2, RoundingMode.HALF_UP);

        String description = trimOrNull(dto.getDescription());
        if (description == null) {
            if (item != null) description = item.getItemName();
            else if (asset != null) description = asset.getAssetName();
        }

        String uom = trimOrNull(dto.getUom());
        if (uom == null) {
            uom = "EACH";
        }

        return PurchaseRequisitionLine.builder()
                .purchaseRequisition(pr)
                .lineType(dto.getLineType())
                .inventoryItem(item)
                .asset(asset)
                .description(description)
                .uom(uom)
                .qtyRequested(dto.getQtyRequested())
                .estimatedUnitPrice(unitPrice.setScale(2, RoundingMode.HALF_UP))
                .lineTotal(lineTotal)
                .build();
    }

    private BigDecimal sumTotal(List<PurchaseRequisitionLine> lines) {
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseRequisitionLine l : lines) {
            if (l.getLineTotal() != null) total = total.add(l.getLineTotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private String generateUniquePrId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD

        for (int attempt = 0; attempt < 30; attempt++) {
            int rand = ThreadLocalRandom.current().nextInt(0, 10000);
            String candidate = String.format("PR-%s-%04d", datePart, rand);
            if (!prRepository.existsByPrId(candidate)) {
                return candidate;
            }
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to generate unique PR ID");
    }

    private String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private void requireNonBlank(String val, String msg) {
        if (val == null || val.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }
    }

    private <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    private void updateIfNotBlank(String value, Consumer<String> setter) {
        if (value != null && !value.trim().isEmpty()) setter.accept(value.trim());
    }

    private PrDetailsResponse toDetails(PurchaseRequisition pr) {
        List<PrLineResponse> lineResponses = pr.getLines().stream().map(line -> {
            InventoryItem item = line.getInventoryItem();
            Asset asset = line.getAsset();

            return PrLineResponse.builder()
                    .id(line.getId())
                    .lineType(line.getLineType())
                    .inventoryItemDbId(item != null ? item.getId() : null)
                    .itemId(item != null ? item.getItemId() : null)
                    .itemName(item != null ? item.getItemName() : null)
                    .assetDbId(asset != null ? asset.getId() : null)
                    .assetId(asset != null ? asset.getAssetId() : null)
                    .assetName(asset != null ? asset.getAssetName() : null)
                    .description(line.getDescription())
                    .uom(line.getUom())
                    .qtyRequested(line.getQtyRequested())
                    .estimatedUnitPrice(line.getEstimatedUnitPrice())
                    .lineTotal(line.getLineTotal())
                    .build();
        }).toList();

        Vendor v = pr.getPreferredVendor();

        return PrDetailsResponse.builder()
                .id(pr.getId())
                .prId(pr.getPrId())
                .requester(pr.getRequester())
                .requestDate(pr.getRequestDate())
                .requiredByDate(pr.getRequiredByDate())
                .priority(pr.getPriority())
                .department(pr.getDepartment())
                .costCenter(pr.getCostCenter())
                .currency(pr.getCurrency())
                .notes(pr.getNotes())
                .preferredVendorId(v != null ? v.getId() : null)
                .preferredVendorName(v != null ? v.getVendorName() : null)
                .requiredForType(pr.getRequiredForType())
                .requiredForReference(pr.getRequiredForReference())
                .status(pr.getStatus())
                .totalEstimatedCost(pr.getTotalEstimatedCost())
                .lines(lineResponses)
                .build();
    }
}
