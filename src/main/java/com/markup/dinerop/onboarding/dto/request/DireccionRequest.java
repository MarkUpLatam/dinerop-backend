package com.markup.dinerop.onboarding.dto.request;

import com.markup.dinerop.onboarding.domain.enums.TipoVivienda;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DireccionRequest {

    @NotBlank
    private String provincia;

    @NotBlank
    private String canton;

    private String barrio;
    private String callePrincipal;
    private String numero;
    private String referenciaUbicacion;
    private TipoVivienda tipoVivienda;
}
