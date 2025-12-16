package com.example.eam.PreventiveMaintenance.Controller;

import com.example.eam.Common.ApiResponse;
import com.example.eam.PreventiveMaintenance.Dto.*;
import com.example.eam.PreventiveMaintenance.Service.PreventiveMaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pm-templates")
@RequiredArgsConstructor
public class PreventiveMaintenanceController {

    private final PreventiveMaintenanceService service;

    @PostMapping
    public ResponseEntity<ApiResponse<PmDetailsResponse>> create(@Valid @RequestBody PmCreateRequest req) {
        PmDetailsResponse data = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.successResponse(HttpStatus.CREATED.value(), "PM template created successfully", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PmDetailsResponse>> get(@PathVariable Long id) {
        PmDetailsResponse data = service.get(id);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "PM template fetched successfully", data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PmDetailsResponse>>> list(Pageable pageable) {
        Page<PmDetailsResponse> data = service.list(pageable);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "PM templates fetched successfully", data));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PmDetailsResponse>> patch(@PathVariable Long id, @RequestBody PmPatchRequest req) {
        PmDetailsResponse data = service.patch(id, req);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "PM template updated successfully", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "PM template deleted successfully", null));
    }

    // Optional but very useful for UI/testing: Generate WO now for this PM template (TIME_BASED only)
    @PostMapping("/{id}/generate-now")
    public ResponseEntity<ApiResponse<Void>> generateNow(@PathVariable Long id) {
        service.generateWorkOrdersDueNowForTemplate(id);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "Work order generation triggered", null));
    }
}

