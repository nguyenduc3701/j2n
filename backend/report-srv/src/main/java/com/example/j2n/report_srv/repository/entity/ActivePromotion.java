package com.example.j2n.report_srv.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "active_promotions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivePromotion {

    @Id
    @Column(name = "promo_id", length = 50)
    private String promoId;

    @Column(name = "domain", length = 20)
    private String domain;

    @Column(name = "promo_name", length = 255)
    private String promoName;

    @Column(name = "end_date")
    private LocalDate endDate;
}
