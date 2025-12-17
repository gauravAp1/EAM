package com.example.eam.Technician.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TechnicianListResponse {

    private List<TechnicianDetailsResponse> technicians;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
