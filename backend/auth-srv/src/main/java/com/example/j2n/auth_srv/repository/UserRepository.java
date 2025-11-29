package com.example.j2n.auth_srv.repository;

import com.example.j2n.auth_srv.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Page<User> findAllByIsDeletedFalse(PageRequest pageRequest);
}
