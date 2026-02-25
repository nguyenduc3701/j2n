package com.example.j2n.config_srv.repository;

import com.example.j2n.config_srv.repository.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, String> {
}
