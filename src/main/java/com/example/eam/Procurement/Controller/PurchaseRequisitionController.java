package com.example.eam.Procurement.Controller;

import com.example.eam.Common.ApiResponse;
import com.example.eam.Procurement.Dto.*;
import com.example.eam.Procurement.Service.PurchaseRequisitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-requisitions")
@RequiredArgsConstructor
public class PurchaseRequisitionController {

    private final PurchaseRequisitionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<PrDetailsResponse>> create(@Valid @RequestBody PrCreateRequest request) {
        PrDetailsResponse data = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.successResponse(HttpStatus.CREATED.value(), "PR created successfully", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrDetailsResponse>> get(@PathVariable Long id) {
        PrDetailsResponse data = service.get(id);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "PR fetched successfully", data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PrDetailsResponse>>> list(Pageable pageable) {
        Page<PrDetailsResponse> data = service.list(pageable);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "PR list fetched successfully", data));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PrDetailsResponse>> patch(@PathVariable Long id, @RequestBody PrPatchRequest request) {
        PrDetailsResponse data = service.patch(id, request);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "PR updated successfully", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "PR deleted successfully", null));
    }
}

