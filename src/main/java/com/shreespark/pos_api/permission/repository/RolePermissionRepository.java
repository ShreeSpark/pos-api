package com.shreespark.pos_api.permission.repository;

import com.shreespark.pos_api.common.enums.Permission;
import com.shreespark.pos_api.common.enums.Role;
import com.shreespark.pos_api.permission.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findAllByTenantIdAndRole(UUID tenantId, Role role);

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.tenantId = :tenantId AND rp.role = :role")
    Set<Permission> findPermissionsByTenantIdAndRole(UUID tenantId, Role role);

    void deleteByTenantIdAndRoleAndPermission(UUID tenantId, Role role, Permission permission);

    boolean existsByTenantIdAndRoleAndPermission(UUID tenantId, Role role, Permission permission);
}
