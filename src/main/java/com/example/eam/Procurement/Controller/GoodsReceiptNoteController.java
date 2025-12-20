package com.example.eam.Procurement.Controller;

import com.example.eam.Common.ApiResponse;
import com.example.eam.Procurement.Dto.CreateGrnRequest;
import com.example.eam.Procurement.Dto.GrnResponse;
import com.example.eam.Procurement.Service.GRNService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/procurement/grn")
@RequiredArgsConstructor
@Validated
public class GoodsReceiptNoteController {

    private final GRNService grnService;

    @PostMapping
    public ResponseEntity<ApiResponse<GrnResponse>> create(@Valid @RequestBody CreateGrnRequest request) {
        GrnResponse data = grnService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.successResponse(HttpStatus.CREATED.value(), "GRN created", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GrnResponse>> get(@PathVariable Long id) {
        GrnResponse data = grnService.get(id);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "GRN fetched", data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GrnResponse>>> list(@RequestParam(required = false) Long poId,
                                                               @RequestParam(required = false) LocalDate from,
                                                               @RequestParam(required = false) LocalDate to) {
        List<GrnResponse> data;
        if (from != null || to != null) {
            data = grnService.listByDateRange(from, to);
        } else {
            data = grnService.list(poId);
        }
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "GRNs fetched", data));
    }

    @GetMapping("/month")
    public ResponseEntity<ApiResponse<List<GrnResponse>>> listByMonth(@RequestParam int year,
                                                                       @RequestParam int month) {
        List<GrnResponse> data = grnService.listByMonth(year, month);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "GRNs fetched for month", data));
    }
}
