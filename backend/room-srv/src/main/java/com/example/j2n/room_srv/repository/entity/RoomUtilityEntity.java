package com.example.j2n.room_srv.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_utilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomUtilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utility_config_id")
    private UtilityConfigEntity utilityConfig;

    @Column(name = "quantity")
    private Integer quantity;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public RoomEntity getRoom() {
        return room;
    }

    public UtilityConfigEntity getUtilityConfig() {
        return utilityConfig;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setRoom(RoomEntity room) {
        this.room = room;
    }

    public void setUtilityConfig(UtilityConfigEntity utilityConfig) {
        this.utilityConfig = utilityConfig;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
