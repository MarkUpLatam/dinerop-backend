package com.markup.dinerop.credit.domain.service.calculation;

import java.math.BigDecimal;

public record LoanCalculationResult(
        BigDecimal tasaMensual,
        BigDecimal cuotaMensual,
        BigDecimal totalPagar,
        BigDecimal interesTotal
) {
    static LoanCalculationResult zero(
            BigDecimal principal,
            BigDecimal cuota,
            int meses
    ) {
        return new LoanCalculationResult(
                BigDecimal.ZERO,
                cuota,
                principal,
                BigDecimal.ZERO
        );
    }
}

