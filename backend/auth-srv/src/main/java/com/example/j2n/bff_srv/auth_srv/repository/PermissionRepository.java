package com.example.j2n.bff_srv.auth_srv.repository;

import com.example.j2n.bff_srv.auth_srv.repository.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}
