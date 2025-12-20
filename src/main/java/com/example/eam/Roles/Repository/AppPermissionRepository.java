package com.example.eam.Roles.Repository;

import com.example.eam.Enum.PermissionModule;
import com.example.eam.Roles.Entity.AppPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppPermissionRepository extends JpaRepository<AppPermission, Long> {
    boolean existsByCode(String code);
    Optional<AppPermission> findByCode(String code);
    List<AppPermission> findByCodeIn(Collection<String> codes);
    List<AppPermission> findByActiveTrueOrderByModuleAscSortOrderAsc();
    List<AppPermission> findByModuleAndActiveTrueOrderBySortOrderAsc(PermissionModule module);
}

