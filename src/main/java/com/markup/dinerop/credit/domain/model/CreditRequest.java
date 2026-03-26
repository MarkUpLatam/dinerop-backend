package com.markup.dinerop.credit.domain.model;

import com.markup.dinerop.credit.domain.model.enums.CreditRequestStatus;
import com.markup.dinerop.credit.domain.model.enums.CreditRequestType;
import com.markup.dinerop.credit.domain.model.enums.CreditType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud_credito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CreditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente")
    private Long clientId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "cedula", nullable = false)
    private String identification;

    @Column(name = "monto_solicitado", nullable = false, precision = 38, scale = 2)
    private BigDecimal amount;

    @Column(name = "provincia")
    private String province;

    @Column(name = "ciudad")
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private CreditRequestType tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_credito", nullable = true, length = 20)
    private CreditType creditType;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private CreditRequestStatus estado;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "plazo_meses")
    private Integer plazoMeses;


    @PrePersist
    void onCreate() {
        if (this.fechaSolicitud == null) {
            this.fechaSolicitud = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = CreditRequestStatus.CREADA;
        }
    }

}




