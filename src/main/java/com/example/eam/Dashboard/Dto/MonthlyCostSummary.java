package com.example.eam.Dashboard.Dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MonthlyCostSummary {

    private String label;
    private BigDecimal totalCost;
}
