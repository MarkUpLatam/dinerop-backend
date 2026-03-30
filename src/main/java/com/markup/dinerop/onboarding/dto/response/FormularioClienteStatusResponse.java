package com.markup.dinerop.onboarding.dto.response;

import com.markup.dinerop.onboarding.domain.enums.EstadoFormulario;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FormularioClienteStatusResponse {
    private boolean formularioCompleto;
    private EstadoFormulario estadoFormulario;
}
