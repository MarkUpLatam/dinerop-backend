package com.markup.dinerop.onboarding.dto.request;

import com.markup.dinerop.onboarding.domain.enums.TipoReferencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReferenciaRequest {

    @NotBlank
    private String nombreCompleto;

    @NotNull
    private TipoReferencia tipo;

    private String parentesco;
    private String telefono;
}
