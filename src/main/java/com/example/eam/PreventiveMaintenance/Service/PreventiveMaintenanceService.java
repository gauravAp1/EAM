package com.example.eam.PreventiveMaintenance.Service;

import com.example.eam.Asset.Entity.Asset;
import com.example.eam.Asset.Repository.AssetRepository;
import com.example.eam.Enum.*;
import com.example.eam.PreventiveMaintenance.Dto.*;
import com.example.eam.PreventiveMaintenance.Entity.*;
import com.example.eam.PreventiveMaintenance.Repository.*;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PreventiveMaintenanceService {

    private final PreventiveMaintenanceTemplateRepository templateRepo;
    private final PreventiveMaintenanceScheduleRepository scheduleRepo;
    private final AssetRepository assetRepository;

    private final WorkOrderRepository workOrderRepository;

    // ---------------- CREATE ----------------

    @Transactional
    public PmDetailsResponse create(PmCreateRequest req) {

        validateAppliesTo(req.getAppliesToType(), req.getAssetDbId(), req.getAssetCategory());
        validateFrequency(req.getFrequencyType(), req.getTimeUnit(), req.getMeterUnit(), req.getFrequencyValue());

        Asset asset = null;
        if (req.getAppliesToType() == PmAppliesToType.ASSET) {
            asset = assetRepository.findById(req.getAssetDbId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Asset not found"));
        }

        PreventiveMaintenance t = PreventiveMaintenance.builder()
                .pmId(generateUniquePmId())
                .pmName(req.getPmName())
                .pmType(req.getPmType())
                .appliesToType(req.getAppliesToType())
                .asset(asset)
                .assetCategory(req.getAppliesToType() == PmAppliesToType.CATEGORY ? safeTrim(req.getAssetCategory()) : null)
                .planStartDate(req.getPlanStartDate())
                .planEndDate(req.getPlanEndDate())
                .frequencyType(req.getFrequencyType())
                .frequencyValue(req.getFrequencyValue())
                .timeUnit(req.getFrequencyType() == PmFrequencyType.TIME_BASED ? req.getTimeUnit() : null)
                .meterUnit(req.getFrequencyType() == PmFrequencyType.METER_BASED ? req.getMeterUnit() : null)
                .graceDays(req.getGraceDays())
                .autoGenerateWo(req.getAutoGenerateWo())
                .leadTimeDays(req.getLeadTimeDays())
                .linkedWorkType(req.getLinkedWorkType())
                .defaultPriority(req.getDefaultPriority())
                .active(true)
                .deleted(false)
                .build();

        PreventiveMaintenance saved = templateRepo.save(t);

        // create schedule state (TIME_BASED uses nextDueDate)
        PreventiveMaintenanceSchedule sch = PreventiveMaintenanceSchedule.builder()
                .template(saved)
                .nextDueDate(saved.getFrequencyType() == PmFrequencyType.TIME_BASED ? saved.getPlanStartDate() : null)
                .lastGeneratedDueDate(null)
                .build();

        scheduleRepo.save(sch);

        return get(saved.getId());
    }

    // ---------------- READ SINGLE ----------------

    @Transactional(readOnly = true)
    public PmDetailsResponse get(Long id) {
        PreventiveMaintenance t = getTemplateOrThrow(id);
        PreventiveMaintenanceSchedule sch = scheduleRepo.findByTemplate_Id(id).orElse(null);
        return mapToResponse(t, sch);
    }

    // ---------------- LIST ----------------

    @Transactional(readOnly = true)
    public Page<PmDetailsResponse> list(Pageable pageable) {
        return templateRepo.findByDeletedFalse(pageable)
                .map(t -> {
                    PreventiveMaintenanceSchedule sch = scheduleRepo.findByTemplate_Id(t.getId()).orElse(null);
                    return mapToResponse(t, sch);
                });
    }

    // ---------------- PATCH UPDATE ----------------

    @Transactional
    public PmDetailsResponse patch(Long id, PmPatchRequest req) {
        PreventiveMaintenance t = getTemplateOrThrow(id);
        PreventiveMaintenanceSchedule sch = scheduleRepo.findByTemplate_Id(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Schedule state missing"));

        // appliesTo changes
        if (req.getAppliesToType() != null) {
            t.setAppliesToType(req.getAppliesToType());
        }

        if (req.getAppliesToType() != null || req.getAssetDbId() != null || req.getAssetCategory() != null) {
            validateAppliesTo(
                    req.getAppliesToType() != null ? req.getAppliesToType() : t.getAppliesToType(),
                    req.getAssetDbId() != null ? req.getAssetDbId() : (t.getAsset() != null ? t.getAsset().getId() : null),
                    req.getAssetCategory() != null ? req.getAssetCategory() : t.getAssetCategory()
            );
        }

        if (req.getAssetDbId() != null) {
            Asset asset = assetRepository.findById(req.getAssetDbId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Asset not found"));
            t.setAsset(asset);
            t.setAssetCategory(null);
            t.setAppliesToType(PmAppliesToType.ASSET);
        }

        if (req.getAssetCategory() != null) {
            t.setAssetCategory(safeTrim(req.getAssetCategory()));
            t.setAsset(null);
            t.setAppliesToType(PmAppliesToType.CATEGORY);
        }

        // frequency validation if updated
        if (req.getFrequencyType() != null || req.getFrequencyValue() != null || req.getTimeUnit() != null || req.getMeterUnit() != null) {
            PmFrequencyType ft = req.getFrequencyType() != null ? req.getFrequencyType() : t.getFrequencyType();
            Integer fv = req.getFrequencyValue() != null ? req.getFrequencyValue() : t.getFrequencyValue();
            TimeFrequencyUnit tu = req.getTimeUnit() != null ? req.getTimeUnit() : t.getTimeUnit();
            MeterFrequencyUnit mu = req.getMeterUnit() != null ? req.getMeterUnit() : t.getMeterUnit();
            validateFrequency(ft, tu, mu, fv);
        }

        updateIfNotBlank(req.getPmName(), t::setPmName);
        if (req.getPmType() != null) t.setPmType(req.getPmType());

        updateIfNotNull(req.getPlanStartDate(), t::setPlanStartDate);
        updateIfNotNull(req.getPlanEndDate(), t::setPlanEndDate);

        if (req.getFrequencyType() != null) t.setFrequencyType(req.getFrequencyType());
        updateIfNotNull(req.getFrequencyValue(), t::setFrequencyValue);
        if (req.getTimeUnit() != null) t.setTimeUnit(req.getTimeUnit());
        if (req.getMeterUnit() != null) t.setMeterUnit(req.getMeterUnit());

        updateIfNotNull(req.getGraceDays(), t::setGraceDays);
        updateIfNotNull(req.getAutoGenerateWo(), t::setAutoGenerateWo);
        updateIfNotNull(req.getLeadTimeDays(), t::setLeadTimeDays);
        if (req.getLinkedWorkType() != null) t.setLinkedWorkType(req.getLinkedWorkType());
        if (req.getDefaultPriority() != null) t.setDefaultPriority(req.getDefaultPriority());
        if (req.getActive() != null) t.setActive(req.getActive());

        // Recalculate schedule safely for TIME_BASED changes:
        if (t.getFrequencyType() == PmFrequencyType.TIME_BASED) {
            if (sch.getLastGeneratedDueDate() != null) {
                sch.setNextDueDate(addInterval(sch.getLastGeneratedDueDate(), t.getFrequencyValue(), t.getTimeUnit()));
            } else {
                sch.setNextDueDate(t.getPlanStartDate());
            }
        } else {
            sch.setNextDueDate(null);
        }

        templateRepo.save(t);
        scheduleRepo.save(sch);
        return get(id);
    }

    // ---------------- DELETE (soft) ----------------

    @Transactional
    public void delete(Long id) {
        PreventiveMaintenance t = getTemplateOrThrow(id);
        t.setDeleted(true);
        t.setActive(false);
        templateRepo.save(t);
    }

    // ---------------- GENERATE NOW (manual trigger for testing/UI) ----------------

    @Transactional
    public void generateWorkOrdersDueNowForTemplate(Long templateId) {
        PreventiveMaintenance t = getTemplateOrThrow(templateId);
        PreventiveMaintenanceSchedule sch = scheduleRepo.findByTemplate_Id(templateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Schedule state missing"));

        if (t.getFrequencyType() != PmFrequencyType.TIME_BASED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Manual due-date generation supported only for TIME_BASED PM");
        }

        LocalDate due = sch.getNextDueDate();
        if (due == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No next due date available");

        generateForTemplateDueDate(t, sch, due);
    }

    // ---------------- AUTOMATION (called by scheduler) ----------------

    @Transactional
    public void generateDueWorkOrders() {
        // load active, auto-generate templates
        templateRepo.findAll().stream()
                .filter(t -> Boolean.FALSE.equals(t.getDeleted()))
                .filter(t -> Boolean.TRUE.equals(t.getActive()))
                .filter(t -> Boolean.TRUE.equals(t.getAutoGenerateWo()))
                .filter(t -> t.getFrequencyType() == PmFrequencyType.TIME_BASED)
                .forEach(t -> {
                    PreventiveMaintenanceSchedule sch = scheduleRepo.findByTemplate_Id(t.getId()).orElse(null);
                    if (sch == null || sch.getNextDueDate() == null) return;

                    LocalDate today = LocalDate.now();
                    LocalDate due = sch.getNextDueDate();

                    if (t.getPlanEndDate() != null && due.isAfter(t.getPlanEndDate())) return;

                    int lead = t.getLeadTimeDays() != null ? t.getLeadTimeDays() : 0;
                    LocalDate generateOnOrAfter = due.minusDays(Math.max(0, lead));

                    if (!today.isBefore(generateOnOrAfter)) {
                        generateForTemplateDueDate(t, sch, due);
                    }
                });
    }

    // ---------------- Internal generator ----------------

    private void generateForTemplateDueDate(PreventiveMaintenance t,
                                           PreventiveMaintenanceSchedule sch,
                                           LocalDate dueDate) {

        // Avoid duplicates (DB unique constraint also protects)
        if (workOrderRepository.existsByPmTemplate_IdAndPmDueDate(t.getId(), dueDate)) {
            return;
        }

        // Resolve assets: either single asset OR category assets
        if (t.getAppliesToType() == PmAppliesToType.ASSET) {
            Asset a = t.getAsset();
            if (a == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PM appliesTo asset missing");

            createPmWorkOrder(t, a, dueDate);
        } else {
            // CATEGORY: create WO for each asset in that category
            String cat = t.getAssetCategory();
            if (cat == null || cat.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PM assetCategory missing");

            // You need this repository method:
            // List<Asset> findByAssetCategory(String assetCategory);
            for (Asset a : assetRepository.findByAssetCategory(cat)) {
                createPmWorkOrder(t, a, dueDate);
            }
        }

        sch.setLastGeneratedDueDate(dueDate);
        sch.setNextDueDate(addInterval(dueDate, t.getFrequencyValue(), t.getTimeUnit()));
        scheduleRepo.save(sch);
    }

    private void createPmWorkOrder(PreventiveMaintenance t, Asset asset, LocalDate dueDate) {
        WorkOrder wo = WorkOrder.builder()
                .workOrderId(generateUniqueWorkOrderId())
                .linkedRequest(null)
                .asset(asset)
                .location(null) // your WO service can auto resolve from asset location if you want
                .workType(t.getLinkedWorkType())
                .priority(t.getDefaultPriority())
                .woTitle("PM: " + t.getPmName())
                .descriptionScope("Auto-generated from PM Template: " + t.getPmId())
                .status(WorkOrderStatus.PLANNED)
                .source(WorkOrderSource.PM)
                .pmTemplate(t)
                .pmDueDate(dueDate)
                .deleted(false)
                .build();

        workOrderRepository.save(wo);
    }

    // ---------------- Helpers ----------------

    private PreventiveMaintenance getTemplateOrThrow(Long id) {
        return templateRepo.findById(id)
                .filter(t -> Boolean.FALSE.equals(t.getDeleted()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PM template not found"));
    }

    private String generateUniquePmId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD
        for (int i = 0; i < 30; i++) {
            int rand = ThreadLocalRandom.current().nextInt(0, 10000);
            String candidate = String.format("PM-%s-%04d", datePart, rand);
            if (!templateRepo.existsByPmId(candidate)) return candidate;
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate unique PM ID");
    }

    private String generateUniqueWorkOrderId() {
        // You already have this logic in WorkOrderService - keep it consistent if you want.
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        for (int attempt = 0; attempt < 30; attempt++) {
            int rand = ThreadLocalRandom.current().nextInt(0, 10000);
            String candidate = String.format("WO-%s-%04d", datePart, rand);
            if (!workOrderRepository.existsByWorkOrderId(candidate)) return candidate;
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate unique Work Order ID");
    }

    private void validateAppliesTo(PmAppliesToType type, Long assetDbId, String assetCategory) {
        if (type == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "appliesToType is required");

        if (type == PmAppliesToType.ASSET) {
            if (assetDbId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "assetDbId is required when appliesToType=ASSET");
        } else {
            if (assetCategory == null || assetCategory.isBlank())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "assetCategory is required when appliesToType=CATEGORY");
        }
    }

    private void validateFrequency(PmFrequencyType ft, TimeFrequencyUnit timeUnit, MeterFrequencyUnit meterUnit, Integer value) {
        if (ft == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "frequencyType is required");
        if (value == null || value < 1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "frequencyValue must be >= 1");

        if (ft == PmFrequencyType.TIME_BASED && timeUnit == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "timeUnit is required when frequencyType=TIME_BASED");
        }
        if (ft == PmFrequencyType.METER_BASED && meterUnit == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "meterUnit is required when frequencyType=METER_BASED");
        }
    }

    private LocalDate addInterval(LocalDate from, Integer value, TimeFrequencyUnit unit) {
        if (from == null || value == null || unit == null) return null;
        return switch (unit) {
            case DAYS -> from.plusDays(value);
            case WEEKS -> from.plusWeeks(value);
            case MONTHS -> from.plusMonths(value);
            case YEARS -> from.plusYears(value);
        };
    }

    private String safeTrim(String s) {
        return s == null ? null : s.trim();
    }

    private <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    private void updateIfNotBlank(String value, Consumer<String> setter) {
        if (value != null && !value.trim().isEmpty()) setter.accept(value.trim());
    }

    private PmDetailsResponse mapToResponse(PreventiveMaintenance t, PreventiveMaintenanceSchedule sch) {
        Asset a = t.getAsset();

        return PmDetailsResponse.builder()
                .id(t.getId())
                .pmId(t.getPmId())
                .pmName(t.getPmName())
                .pmType(t.getPmType())
                .appliesToType(t.getAppliesToType())
                .assetDbId(a != null ? a.getId() : null)
                .assetId(a != null ? a.getAssetId() : null)
                .assetName(a != null ? a.getAssetName() : null)
                .assetCategory(t.getAppliesToType() == PmAppliesToType.CATEGORY ? t.getAssetCategory() : (a != null ? a.getAssetCategory() : null))
                .planStartDate(t.getPlanStartDate())
                .planEndDate(t.getPlanEndDate())
                .frequencyType(t.getFrequencyType())
                .frequencyValue(t.getFrequencyValue())
                .timeUnit(t.getTimeUnit())
                .meterUnit(t.getMeterUnit())
                .graceDays(t.getGraceDays())
                .autoGenerateWo(t.getAutoGenerateWo())
                .leadTimeDays(t.getLeadTimeDays())
                .linkedWorkType(t.getLinkedWorkType())
                .defaultPriority(t.getDefaultPriority())
                .active(t.getActive())
                .nextDueDate(sch != null ? sch.getNextDueDate() : null)
                .lastGeneratedDueDate(sch != null ? sch.getLastGeneratedDueDate() : null)
                .build();
    }
}

