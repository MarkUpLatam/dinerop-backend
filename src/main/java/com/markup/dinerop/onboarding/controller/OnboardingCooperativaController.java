package com.markup.dinerop.onboarding.controller;

import com.markup.dinerop.auth.entity.User;
import com.markup.dinerop.onboarding.application.usecase.ObtenerOnboardingUnificadoUseCase;
import com.markup.dinerop.onboarding.application.usecase.SolicitarGaranteUseCase;
import com.markup.dinerop.onboarding.dto.response.OnboardingUnificadoResponse;
import com.markup.dinerop.onboarding.infrastructure.mapper.OnboardingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboarding/cooperativa")
@RequiredArgsConstructor
public class OnboardingCooperativaController {

    private final SolicitarGaranteUseCase solicitarGaranteUC;
    private final ObtenerOnboardingUnificadoUseCase obtenerOnboardingUC;
    private final OnboardingMapper mapper;

    @PostMapping("/{solicitudId}/solicitar-garante")
    @PreAuthorize("hasRole('COOPERATIVE')")
    public OnboardingUnificadoResponse solicitarGarante(
            @PathVariable Long solicitudId,
            @AuthenticationPrincipal User user
    ) {
        return mapper.toOnboardingUnificadoResponse(
                solicitarGaranteUC.execute(solicitudId, user.getCooperativaId())
        );
    }

    @GetMapping("/{solicitudId}")
    @PreAuthorize("hasRole('COOPERATIVE')")
    public OnboardingUnificadoResponse obtenerOnboarding(
            @PathVariable Long solicitudId
    ) {
        return mapper.toOnboardingUnificadoResponse(
                obtenerOnboardingUC.execute(solicitudId)
        );
    }
}
