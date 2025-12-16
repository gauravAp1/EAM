package com.example.eam.ServiceContract.Controller;

import com.example.eam.Common.ApiResponse;
import com.example.eam.ServiceContract.Dto.*;
import com.example.eam.ServiceContract.Service.ServiceContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service-contracts")
@RequiredArgsConstructor
public class ServiceContractController {

    private final ServiceContractService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceContractDetailsResponse>> create(@Valid @RequestBody ServiceContractCreateRequest req) {
        ServiceContractDetailsResponse data = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.successResponse(HttpStatus.CREATED.value(), "Service contract created successfully", data));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceContractDetailsResponse>> patch(@PathVariable Long id,
                                                                            @RequestBody ServiceContractPatchRequest req) {
        ServiceContractDetailsResponse data = service.patch(id, req);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "Service contract updated successfully", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceContractDetailsResponse>> get(@PathVariable Long id) {
        ServiceContractDetailsResponse data = service.get(id);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "Service contract fetched successfully", data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ServiceContractDetailsResponse>>> list(Pageable pageable) {
        Page<ServiceContractDetailsResponse> data = service.list(pageable);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "Service contracts fetched successfully", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "Service contract deleted successfully", null));
    }
}

