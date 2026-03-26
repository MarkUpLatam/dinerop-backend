package com.markup.dinerop.credit.domain.model;

import com.markup.dinerop.credit.domain.model.enums.CreditType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "solicitud_cooperativa_cotizacion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cotizacion_sc",
                columnNames = "solicitud_cooperativa_id"
        )
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SolicitudCooperativaCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solicitud_cooperativa_id", nullable = false)
    private Long solicitudCooperativaId;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal monto;

    @Column(name = "plazo_meses", nullable = false)
    private Integer plazoMeses;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_credito", nullable = false)
    private CreditType tipoCredito;

    @Column(name = "tasa_anual", nullable = false, precision = 6, scale = 3)
    private BigDecimal tasaAnual;

    @Column(name = "tasa_mensual", nullable = false, precision = 10, scale = 8)
    private BigDecimal tasaMensual;

    @Column(name = "cuota_mensual", nullable = false, precision = 38, scale = 2)
    private BigDecimal cuotaMensual;

    @Column(name = "total_pagar", nullable = false, precision = 38, scale = 2)
    private BigDecimal totalPagar;

    @Column(name = "interes_total", nullable = false, precision = 38, scale = 2)
    private BigDecimal interesTotal;

    @Column(name = "fecha_calculo", nullable = false)
    private LocalDateTime fechaCalculo;

    @PrePersist
    void onCreate() {
        fechaCalculo = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        fechaCalculo = LocalDateTime.now();
    }
}

