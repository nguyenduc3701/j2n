package com.example.j2n.room_srv.repository.entity;

import com.example.j2n.room_srv.enums.BillStatus;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillEntity {

    @Id
    private String id; // UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @Column(name = "renter_id")
    private Long renterId;

    @Column(name = "billing_month", nullable = false)
    private Integer billingMonth;

    @Column(name = "electricity_old_index")
    private Integer electricityOldIndex;

    @Column(name = "electricity_new_index")
    private Integer electricityNewIndex;

    @Column(name = "electricity_usage")
    private Integer electricityUsage;

    @Column(name = "water_usage")
    private Integer waterUsage;

    @Column(name = "service_fees")
    private BigDecimal serviceFees;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BillStatus status;

    @Column(name = "order_id")
    private Long orderId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
