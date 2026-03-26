package com.markup.dinerop.cooperative.dto;

import com.markup.dinerop.cooperative.domain.entity.enums.CreditType;

import java.math.BigDecimal;

public record CooperativeRateDto(
        Long cooperativaId,
        CreditType tipoCredito,
        BigDecimal tasaAnual
) {}
