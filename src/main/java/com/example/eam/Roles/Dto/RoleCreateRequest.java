package com.example.eam.Roles.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class RoleCreateRequest {
    @NotBlank
    private String name;

    private String description;

    @NotEmpty
    private Set<String> permissionCodes;
}

