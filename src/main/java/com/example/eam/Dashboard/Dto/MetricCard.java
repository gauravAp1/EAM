package com.example.eam.Dashboard.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricCard {

    private long value;
    private Double changePercentage;
    private Boolean trendUp;
}
