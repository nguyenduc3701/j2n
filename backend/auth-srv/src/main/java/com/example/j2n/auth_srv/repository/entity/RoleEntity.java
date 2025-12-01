package com.example.j2n.auth_srv.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "roles")
@AllArgsConstructor
@RequiredArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String name;

    @Column(length = 255)
    private String description;

    // SỬA: Thêm CascadeType.ALL để khi lưu Role, Permissions mới cũng được lưu (nếu
    // cần)
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @ToString.Exclude
    private Set<PermissionEntity> permissions = new HashSet<>();

    // Helper method để quản lý quan hệ hai chiều
    public void addPermission(PermissionEntity permission) {
        this.permissions.add(permission);
        permission.getRoles().add(this);
    }
}
