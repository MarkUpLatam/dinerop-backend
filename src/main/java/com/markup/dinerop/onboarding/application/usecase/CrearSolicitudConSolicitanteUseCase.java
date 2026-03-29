package com.markup.dinerop.onboarding.application.usecase;

import com.markup.dinerop.onboarding.application.service.OnboardingService;
import com.markup.dinerop.onboarding.domain.entity.SolicitudOnboarding;
import com.markup.dinerop.onboarding.dto.request.SolicitudOnboardingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrearSolicitudConSolicitanteUseCase {

    private final OnboardingService onboardingService;

    public SolicitudOnboarding execute(Long clientId, SolicitudOnboardingRequest request) {
        return onboardingService.crearSolicitudConSolicitante(clientId, request);
    }
}
