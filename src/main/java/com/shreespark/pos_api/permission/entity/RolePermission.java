package com.shreespark.pos_api.permission.entity;

import com.shreespark.pos_api.common.enums.Permission;
import com.shreespark.pos_api.common.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "role_permissions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "role", "permission"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Permission permission;
}
