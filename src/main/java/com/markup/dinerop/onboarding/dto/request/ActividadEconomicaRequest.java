package com.markup.dinerop.onboarding.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActividadEconomicaRequest {
    private String nombreNegocio;
    private String direccionNegocio;
    private String tiempoActividad;
    private String telefonoNegocio;
}
