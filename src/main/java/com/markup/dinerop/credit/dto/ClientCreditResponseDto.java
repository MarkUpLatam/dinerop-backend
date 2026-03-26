package com.markup.dinerop.credit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientCreditResponseDto {

    private Long solicitudId;
    private String estado;                 // solicitud_cooperativa.estado
    private BigDecimal monto;
    private String tipo;
    private LocalDateTime fechaSolicitud;
    private Integer cantidadSolicitudesEnviadas;
}

