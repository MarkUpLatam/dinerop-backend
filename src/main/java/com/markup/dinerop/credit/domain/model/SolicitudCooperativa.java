package com.markup.dinerop.credit.domain.model;

import com.markup.dinerop.credit.domain.model.enums.SolicitudCooperativaStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "solicitud_cooperativa",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_solicitud_coop",
                columnNames = {"solicitud_id", "cooperativa_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudCooperativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo actual (SE QUEDA)
    @Column(name = "solicitud_id", nullable = false)
    private Long solicitudId;

    // RELACIÃ“N JPA (NUEVA)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "solicitud_id",
            insertable = false,
            updatable = false
    )
    private CreditRequest creditRequest;

    @Column(name = "cooperativa_id", nullable = false)
    private Long cooperativaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private SolicitudCooperativaStatus estado;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    void onCreate() {
        this.fechaEnvio = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = SolicitudCooperativaStatus.ENVIADA;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}


