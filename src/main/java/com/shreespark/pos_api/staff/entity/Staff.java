package com.shreespark.pos_api.staff.entity;

import com.shreespark.pos_api.common.BaseEntity;
import com.shreespark.pos_api.common.enums.Permission;
import com.shreespark.pos_api.common.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "staff_permissions", joinColumns = @JoinColumn(name = "staff_id"))
    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private Set<Permission> permissions;
}
