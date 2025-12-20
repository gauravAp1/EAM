package com.example.eam.Roles.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import com.example.eam.Enum.PermissionModule;

@Data
@Builder
public class PermissionModuleGroupResponse {
    private PermissionModule module;
    private List<PermissionResponse> permissions;
}

