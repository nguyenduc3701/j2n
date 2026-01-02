package com.example.j2n.image_srv.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "images", indexes = {
        @Index(name = "idx_owner", columnList = "owner_type, owner_id"),
        @Index(name = "idx_type", columnList = "image_type"),
        @Index(name = "idx_images_is_deleted", columnList = "is_deleted")
})
@AllArgsConstructor
@NoArgsConstructor
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_type", length = 50, nullable = false)
    private String ownerType;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Column(name = "file_path", length = 500, nullable = false)
    private String filePath;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "image_type", length = 50)
    private String imageType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
