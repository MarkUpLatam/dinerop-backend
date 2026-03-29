package com.markup.dinerop.onboarding.application.usecase;

import com.markup.dinerop.onboarding.application.service.OnboardingService;
import com.markup.dinerop.onboarding.domain.entity.SolicitudOnboarding;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObtenerOnboardingUnificadoUseCase {

    private final OnboardingService service;

    public SolicitudOnboarding execute(Long solicitudId) {
        return service.obtenerOnboardingUnificado(solicitudId);
    }
}
