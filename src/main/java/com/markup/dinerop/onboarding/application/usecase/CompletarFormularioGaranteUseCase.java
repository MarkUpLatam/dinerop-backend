package com.markup.dinerop.onboarding.application.usecase;

import com.markup.dinerop.onboarding.application.service.OnboardingService;
import com.markup.dinerop.onboarding.domain.entity.SolicitudOnboarding;
import com.markup.dinerop.onboarding.dto.request.PersonaOnboardingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompletarFormularioGaranteUseCase {

    private final OnboardingService service;

    public SolicitudOnboarding execute(Long clientId, PersonaOnboardingRequest request) {
        return service.crearYCompletarGaranteDesdeCliente(clientId, request);
    }
}
