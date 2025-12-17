package com.example.eam.TechnicianTeam.Controller;

import com.example.eam.Common.ApiResponse;
import com.example.eam.TechnicianTeam.Dto.*;
import com.example.eam.TechnicianTeam.Service.TechnicianTeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/technician-teams")
@RequiredArgsConstructor
@Validated
public class TechnicianTeamController {

    private final TechnicianTeamService technicianTeamService;

    @PostMapping
    public ResponseEntity<ApiResponse<TechnicianTeamDetailsResponse>> create(@Valid @RequestBody TechnicianTeamCreateRequest request) {
        TechnicianTeamDetailsResponse data = technicianTeamService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.successResponse(HttpStatus.CREATED.value(), "Technician team created successfully", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TechnicianTeamDetailsResponse>> get(@PathVariable Long id) {
        TechnicianTeamDetailsResponse data = technicianTeamService.getTeam(id);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "Technician team fetched successfully", data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TechnicianTeamListResponse>> list(Pageable pageable) {
        TechnicianTeamListResponse data = technicianTeamService.listTeams(pageable);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "Technician teams fetched successfully", data));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TechnicianTeamDetailsResponse>> patch(@PathVariable Long id,
                                                                            @RequestBody TechnicianTeamPatchRequest request) {
        TechnicianTeamDetailsResponse data = technicianTeamService.patchTeam(id, request);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "Technician team updated successfully", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        technicianTeamService.deleteTeam(id);
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "Technician team deleted successfully", null));
    }
}
