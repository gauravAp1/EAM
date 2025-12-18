package com.example.eam.Dashboard.Controller;

import com.example.eam.Common.ApiResponse;
import com.example.eam.Dashboard.Dto.DashboardResponse;
import com.example.eam.Dashboard.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        DashboardResponse response = dashboardService.getDashboard();
        return ResponseEntity.ok(
                ApiResponse.successResponse(HttpStatus.OK.value(),
                        "Dashboard data fetched successfully", response)
        );
    }
}
