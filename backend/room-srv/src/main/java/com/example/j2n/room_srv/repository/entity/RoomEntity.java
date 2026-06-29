package com.example.j2n.room_srv.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false, unique = true)
    private String roomNumber;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(name = "area")
    private String area;

    @Column(name = "max_people")
    private Integer maxPeople;

    @Column(name = "status")
    private String status;

    @Column(name = "current_electric_index")
    private Integer currentElectricIndex;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDeleted = false;

    @Column(name = "is_immutable", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isImmutable = false;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomAssetEntity> assets;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomMemberEntity> members;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomFeeEntity> fees;
}
