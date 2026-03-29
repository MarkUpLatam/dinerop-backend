package com.markup.dinerop.onboarding.application.service;

import com.markup.dinerop.onboarding.domain.entity.SolicitudOnboarding;
import com.markup.dinerop.onboarding.dto.request.PersonaOnboardingRequest;
import com.markup.dinerop.onboarding.dto.request.SolicitudOnboardingRequest;

public interface OnboardingService {

    SolicitudOnboarding crearSolicitudConSolicitante(
            Long clientId,
            SolicitudOnboardingRequest request
    );

    SolicitudOnboarding solicitarGarante(Long solicitudId, Long cooperativaId);

    SolicitudOnboarding crearYCompletarGaranteDesdeCliente(
            Long clientId,
            PersonaOnboardingRequest request
    );

    SolicitudOnboarding obtenerOnboardingUnificado(Long solicitudId);
}
