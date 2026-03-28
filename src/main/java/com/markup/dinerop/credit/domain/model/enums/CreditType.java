package com.markup.dinerop.credit.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CreditType {
    MICROCREDITO,
    CONSUMO;

    @JsonCreator
    public static CreditType fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (CreditType t : values()) {
            if (t.name().equalsIgnoreCase(value)) return t;
        }
        // Valor no reconocido (ej: "INVERSION") -> retorna null en vez de explotar
        return null;
    }
}
