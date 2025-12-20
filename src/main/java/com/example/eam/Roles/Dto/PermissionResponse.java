package com.example.eam.Roles.Dto;


import com.example.eam.Enum.PermissionAction;
import com.example.eam.Enum.PermissionModule;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionResponse {
    private Long id;
    private String code;
    private PermissionModule module;
    private PermissionAction action;
    private String label;
    private String description;
}

