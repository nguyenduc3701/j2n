package com.example.j2n.auth_srv.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_role_id", columnList = "role_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_is_deleted", columnList = "is_deleted")
})
@AllArgsConstructor
@RequiredArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 100, unique = true)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    private LocalDate birth;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "room_id")
    private Long roomId;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String company;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    public enum Status {
        ACTIVE,
        INACTIVE,
        SUSPENDED
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
