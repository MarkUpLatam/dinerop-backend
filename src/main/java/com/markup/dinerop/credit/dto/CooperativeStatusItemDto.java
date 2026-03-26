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
public class CooperativeStatusItemDto {
    private Long cooperativaId;
    private String nombreCooperativa;
    private String estado; // PRE_APROBADA
    private LocalDateTime fechaActualizacion;

    private BigDecimal monto;
    private Integer plazoMeses;
    private String tipoCredito;

    private BigDecimal tasaAnual;
    private BigDecimal cuotaMensual;
    private BigDecimal totalPagar;
    private BigDecimal interesTotal;


}

