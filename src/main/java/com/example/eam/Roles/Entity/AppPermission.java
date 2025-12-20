package com.example.eam.Roles.Entity;

import com.example.eam.Enum.PermissionAction;
import com.example.eam.Enum.PermissionModule;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "app_permissions",
    uniqueConstraints = @UniqueConstraint(name = "uk_permission_code", columnNames = "code"),
    indexes = {
        @Index(name = "idx_permission_module", columnList = "module"),
        @Index(name = "idx_permission_code", columnList = "code")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. ASSET_CREATE
    @Column(name = "code", nullable = false, unique = true, length = 80)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "module", nullable = false, length = 64)
    private PermissionModule module;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 32)
    private PermissionAction action;

    @Column(name = "label", nullable = false, length = 150)
    private String label; // UI friendly: "Create Asset"

    @Column(name = "description", length = 500)
    private String description;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "sort_order")
    private Integer sortOrder;
}

