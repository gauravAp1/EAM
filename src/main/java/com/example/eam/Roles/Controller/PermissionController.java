package com.example.eam.Roles.Controller;

import com.example.eam.Common.ApiResponse;
import com.example.eam.Roles.Dto.PermissionModuleGroupResponse;
import com.example.eam.Roles.Service.PermissionCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionCatalogService catalogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionModuleGroupResponse>>> catalog() {
        List<PermissionModuleGroupResponse> data = catalogService.getCatalogGroupedByModule();
        return ResponseEntity.ok(ApiResponse.successResponse(HttpStatus.OK.value(), "Permissions fetched successfully", data));
    }
}

