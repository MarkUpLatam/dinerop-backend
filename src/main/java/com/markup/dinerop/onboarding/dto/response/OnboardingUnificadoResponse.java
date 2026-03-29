package com.markup.dinerop.onboarding.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingUnificadoResponse {
    private Long solicitudId;
    private String destinoCredito;
    private String estado;
    private List<PersonaOnboardingResponse> personas;
}
