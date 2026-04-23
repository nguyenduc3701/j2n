package com.example.j2n.payment_srv.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "order_info",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_user_item",
        columnNames = {"user_id", "item_id", "item_type"}
    ),
    indexes = @Index(name = "idx_user_id", columnList = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "item_id", length = 255, nullable = false)
    private String itemId;

    @Column(name = "item_type", length = 100, nullable = false)
    private String itemType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
