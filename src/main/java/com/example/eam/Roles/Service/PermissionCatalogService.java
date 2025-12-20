package com.example.eam.Roles.Service;

import com.example.eam.Enum.PermissionModule;
import com.example.eam.Roles.Dto.PermissionModuleGroupResponse;
import com.example.eam.Roles.Dto.PermissionResponse;
import com.example.eam.Roles.Entity.AppPermission;
import com.example.eam.Roles.Repository.AppPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionCatalogService {

    private final AppPermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<PermissionModuleGroupResponse> getCatalogGroupedByModule() {
        List<AppPermission> perms = permissionRepository.findByActiveTrueOrderByModuleAscSortOrderAsc();

        Map<?, List<AppPermission>> grouped = perms.stream()
                .collect(Collectors.groupingBy(AppPermission::getModule, LinkedHashMap::new, Collectors.toList()));

        List<PermissionModuleGroupResponse> resp = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            var module = (PermissionModule) entry.getKey();
            var list = entry.getValue().stream()
                    .map(this::toResponse)
                    .toList();

            resp.add(PermissionModuleGroupResponse.builder()
                    .module(module)
                    .permissions(list)
                    .build());
        }
        return resp;
    }

    private PermissionResponse toResponse(AppPermission p) {
        return PermissionResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .module(p.getModule())
                .action(p.getAction())
                .label(p.getLabel())
                .description(p.getDescription())
                .build();
    }
}

