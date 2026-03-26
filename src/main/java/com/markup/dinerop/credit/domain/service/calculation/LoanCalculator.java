package com.markup.dinerop.credit.domain.service.calculation;

import java.math.*;

public class LoanCalculator {

    public static LoanCalculationResult calculate(
            BigDecimal principal,
            BigDecimal annualRatePercent,
            int termMonths
    ) {
        if (annualRatePercent.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal cuota = principal.divide(
                    BigDecimal.valueOf(termMonths),
                    2,
                    RoundingMode.HALF_UP
            );
            return LoanCalculationResult.zero(principal, cuota, termMonths);
        }

        BigDecimal monthlyRate = annualRatePercent
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal denominator = BigDecimal.ONE.subtract(
                onePlusR.pow(-termMonths, MathContext.DECIMAL64)
        );

        BigDecimal cuota = principal
                .multiply(monthlyRate)
                .divide(denominator, 2, RoundingMode.HALF_UP);

        BigDecimal totalPagar = cuota.multiply(BigDecimal.valueOf(termMonths));
        BigDecimal interesTotal = totalPagar.subtract(principal);

        return new LoanCalculationResult(
                monthlyRate,
                cuota,
                totalPagar,
                interesTotal
        );
    }
}

