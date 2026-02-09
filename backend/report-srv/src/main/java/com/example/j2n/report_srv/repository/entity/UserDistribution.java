package com.example.j2n.report_srv.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_distribution")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDistribution {

    @Id
    @Column(name = "user_type", length = 50)
    private String userType;

    @Column(name = "count")
    private Long count;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
