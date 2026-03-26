package com.markup.dinerop.cooperative.domain.entity;

import com.markup.dinerop.cooperative.domain.entity.enums.CreditType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cooperativa_tasa",
        uniqueConstraints = @UniqueConstraint(name = "uk_cooperativa_tasa_coop_tipo", columnNames = {"cooperativa_id", "tipo_credito"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CooperativeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cooperativa_id", nullable = false)
    private Long cooperativaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_credito", nullable = false, length = 20)
    private CreditType tipoCredito;

    @Column(name = "tasa_anual", nullable = false, precision = 6, scale = 3)
    private BigDecimal tasaAnual;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    void onCreate() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
