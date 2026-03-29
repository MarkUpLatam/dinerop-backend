package com.markup.dinerop.onboarding.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudOnboardingRequest {

    @NotBlank
    private String destinoCredito;

    @NotNull
    @Valid
    private PersonaOnboardingRequest solicitante;

    @Valid
    private PersonaOnboardingRequest conyuge;
}
