package com.example.eam.Dashboard.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private MetricCard openServiceRequests;
    private MetricCard activeWorkOrders;
    private MetricCard overdueTasks;
    private MetricCard criticalAssetsDown;

    private WorkOrderStatusBreakdown workOrdersByStatus;
    private List<MonthlyCostSummary> maintenanceCostSummary;
    private List<RecentWorkOrderDto> recentWorkOrders;
}
