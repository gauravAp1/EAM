package com.example.eam.Dashboard.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkOrderStatusBreakdown {

    private long completed;
    private long inProgress;
    private long pending;
}
