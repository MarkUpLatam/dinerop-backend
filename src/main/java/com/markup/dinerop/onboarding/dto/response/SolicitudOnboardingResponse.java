package com.markup.dinerop.onboarding.dto.response;

import com.markup.dinerop.onboarding.domain.enums.EstadoOnboarding;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudOnboardingResponse {
    private Long id;
    private EstadoOnboarding estado;
    private String destinoCredito;
    private LocalDateTime fechaCreacion;
}
