package com.example.eam.WorkOrder.Dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkOrderListResponse {
    private List<WorkOrderDetailsResponse> workOrders;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}

