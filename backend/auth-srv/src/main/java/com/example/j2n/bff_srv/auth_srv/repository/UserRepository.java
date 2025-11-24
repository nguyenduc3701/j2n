package com.example.j2n.bff_srv.auth_srv.repository;

import com.example.j2n.bff_srv.auth_srv.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
