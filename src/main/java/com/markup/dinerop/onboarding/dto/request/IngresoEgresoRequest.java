package com.markup.dinerop.onboarding.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngresoEgresoRequest {

    @NotNull
    private Double ingresoMensual;

    @NotNull
    private Double egresoMensual;
}
