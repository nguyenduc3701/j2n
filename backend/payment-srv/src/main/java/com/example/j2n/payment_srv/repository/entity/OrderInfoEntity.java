package com.example.j2n.payment_srv.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "order_info",
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_user_item", columnList = "user_id, item_id, item_type")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE order_info SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted = false")
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

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
