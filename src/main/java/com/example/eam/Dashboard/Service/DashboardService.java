package com.example.eam.Dashboard.Service;

import com.example.eam.Asset.Repository.AssetRepository;
import com.example.eam.Dashboard.Dto.*;
import com.example.eam.Enum.*;
import com.example.eam.ServiceMaintenance.Repository.ServiceMaintenanceRepository;
import com.example.eam.WorkOrder.Entity.WorkOrder;
import com.example.eam.WorkOrder.Repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final Set<ServiceRequestStatus> OPEN_SR_STATUSES =
            EnumSet.of(ServiceRequestStatus.NEW, ServiceRequestStatus.UNDER_REVIEW);

    private static final Set<WorkOrderStatus> ACTIVE_WORK_ORDER_STATUSES =
            EnumSet.of(WorkOrderStatus.NEW, WorkOrderStatus.APPROVED, WorkOrderStatus.SCHEDULED, WorkOrderStatus.IN_PROGRESS);

    private static final Set<WorkOrderStatus> COMPLETED_STATUSES =
            EnumSet.of(WorkOrderStatus.COMPLETED, WorkOrderStatus.CLOSED);

    private static final Set<WorkOrderStatus> PENDING_STATUSES =
            EnumSet.of(WorkOrderStatus.NEW, WorkOrderStatus.APPROVED, WorkOrderStatus.SCHEDULED);

    private final ServiceMaintenanceRepository serviceMaintenanceRepository;
    private final WorkOrderRepository workOrderRepository;
    private final AssetRepository assetRepository;

    public DashboardResponse getDashboard() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.minusWeeks(1);
        LocalDateTime prevWeekStart = now.minusWeeks(2);

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        LocalDate twoWeeksAgo = today.minusDays(14);

        MetricCard openServiceRequests = buildMetric(
                serviceMaintenanceRepository.countByDeletedFalseAndStatusIn(OPEN_SR_STATUSES),
                serviceMaintenanceRepository.countByDeletedFalseAndStatusInAndRequestDateBetween(OPEN_SR_STATUSES, weekStart, now),
                serviceMaintenanceRepository.countByDeletedFalseAndStatusInAndRequestDateBetween(OPEN_SR_STATUSES, prevWeekStart, weekStart)
        );

        MetricCard activeWorkOrders = buildMetric(
                workOrderRepository.countByStatusInAndDeletedFalse(ACTIVE_WORK_ORDER_STATUSES),
                workOrderRepository.countByStatusInAndCreatedAtBetweenAndDeletedFalse(ACTIVE_WORK_ORDER_STATUSES, weekStart, now),
                workOrderRepository.countByStatusInAndCreatedAtBetweenAndDeletedFalse(ACTIVE_WORK_ORDER_STATUSES, prevWeekStart, weekStart)
        );

        MetricCard overdueTasks = buildMetric(
                workOrderRepository.countByTargetCompletionDateBeforeAndStatusInAndDeletedFalse(today, ACTIVE_WORK_ORDER_STATUSES),
                workOrderRepository.countByTargetCompletionDateBetweenAndStatusInAndDeletedFalse(weekAgo, today, ACTIVE_WORK_ORDER_STATUSES),
                workOrderRepository.countByTargetCompletionDateBetweenAndStatusInAndDeletedFalse(twoWeeksAgo, weekAgo, ACTIVE_WORK_ORDER_STATUSES)
        );

        long criticalAssets = assetRepository.countByCriticalityAndStatusIn(
                AssetCriticality.CRITICAL,
                EnumSet.of(AssetStatus.OUT_OF_SERVICE, AssetStatus.UNDER_MAINTENANCE)
        );

        MetricCard criticalAssetsDown = MetricCard.builder()
                .value(criticalAssets)
                .changePercentage(null)
                .trendUp(null)
                .build();

        WorkOrderStatusBreakdown workOrdersByStatus = WorkOrderStatusBreakdown.builder()
                .completed(workOrderRepository.countByStatusInAndDeletedFalse(COMPLETED_STATUSES))
                .inProgress(workOrderRepository.countByStatusAndDeletedFalse(WorkOrderStatus.IN_PROGRESS))
                .pending(workOrderRepository.countByStatusInAndDeletedFalse(PENDING_STATUSES))
                .build();

        List<MonthlyCostSummary> costSummary = buildCostSummary(now);
        List<RecentWorkOrderDto> recentWorkOrders = mapRecentWorkOrders();

        return DashboardResponse.builder()
                .openServiceRequests(openServiceRequests)
                .activeWorkOrders(activeWorkOrders)
                .overdueTasks(overdueTasks)
                .criticalAssetsDown(criticalAssetsDown)
                .workOrdersByStatus(workOrdersByStatus)
                .maintenanceCostSummary(costSummary)
                .recentWorkOrders(recentWorkOrders)
                .build();
    }

    private MetricCard buildMetric(long value, long currentWindow, long previousWindow) {
        Double change = calculateChangePercentage(currentWindow, previousWindow);
        Boolean trendUp = change != null ? change >= 0 : null;
        return MetricCard.builder()
                .value(value)
                .changePercentage(change)
                .trendUp(trendUp)
                .build();
    }

    private Double calculateChangePercentage(long current, long previous) {
        if (previous == 0) {
            return null;
        }
        double diff = current - previous;
        return (diff / previous) * 100d;
    }

    private List<MonthlyCostSummary> buildCostSummary(LocalDateTime now) {
        YearMonth currentMonth = YearMonth.from(now);
        YearMonth sixMonthsAgo = currentMonth.minusMonths(5);
        LocalDateTime start = sixMonthsAgo.atDay(1).atStartOfDay();

        List<WorkOrder> workOrders = workOrderRepository.findByDeletedFalseAndCreatedAtBetween(start, now);
        Map<YearMonth, BigDecimal> totals = new LinkedHashMap<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth ym = currentMonth.minusMonths(i);
            totals.put(ym, BigDecimal.ZERO);
        }

        for (WorkOrder wo : workOrders) {
            if (wo.getCreatedAt() == null) continue;
            YearMonth ym = YearMonth.from(wo.getCreatedAt());
            if (!totals.containsKey(ym)) continue;
            BigDecimal cost = Optional.ofNullable(wo.getActualTotalCost())
                    .orElse(Optional.ofNullable(wo.getEstimatedTotalCost()).orElse(BigDecimal.ZERO));
            totals.put(ym, totals.get(ym).add(cost));
        }

        List<MonthlyCostSummary> summaries = new ArrayList<>();
        for (Map.Entry<YearMonth, BigDecimal> entry : totals.entrySet()) {
            String label = entry.getKey().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            summaries.add(MonthlyCostSummary.builder()
                    .label(label)
                    .totalCost(entry.getValue())
                    .build());
        }
        return summaries;
    }

    private List<RecentWorkOrderDto> mapRecentWorkOrders() {
        return workOrderRepository.findTop5ByDeletedFalseOrderByCreatedAtDesc().stream()
                .map(wo -> RecentWorkOrderDto.builder()
                        .workOrderId(wo.getWorkOrderId())
                        .title(wo.getWoTitle())
                        .asset(wo.getAsset() != null ? wo.getAsset().getAssetName() : null)
                        .technician(wo.getAssignedTechnician() != null ? wo.getAssignedTechnician().getFullName() : null)
                        .dueDate(wo.getTargetCompletionDate())
                        .priority(wo.getPriority())
                        .status(wo.getStatus())
                        .build())
                .toList();
    }
}
