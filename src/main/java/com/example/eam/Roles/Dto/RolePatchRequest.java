package com.example.eam.Roles.Dto;

import lombok.Data;

import java.util.Set;

@Data
public class RolePatchRequest {
    private String name;
    private String description;
    private Boolean active;

    // If provided => replace role permissions
    private Set<String> permissionCodes;
}

