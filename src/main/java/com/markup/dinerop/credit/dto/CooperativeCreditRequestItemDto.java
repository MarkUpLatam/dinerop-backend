package com.markup.dinerop.credit.dto;

import com.markup.dinerop.credit.domain.model.enums.CreditRequestType;
import com.markup.dinerop.credit.domain.model.enums.SolicitudCooperativaStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CooperativeCreditRequestItemDto(
        Long solicitudId,
        BigDecimal montoSolicitado,
        CreditRequestType tipo,
        LocalDateTime fechaSolicitud,
        SolicitudCooperativaStatus estado
) {}

