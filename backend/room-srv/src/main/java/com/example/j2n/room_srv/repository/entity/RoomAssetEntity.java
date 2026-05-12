package com.example.j2n.room_srv.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id", nullable = false)
    private AssetEntity asset;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
