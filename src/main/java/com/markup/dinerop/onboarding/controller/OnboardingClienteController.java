package com.markup.dinerop.onboarding.controller;

import com.markup.dinerop.auth.entity.User;
import com.markup.dinerop.onboarding.application.usecase.CompletarFormularioGaranteUseCase;
import com.markup.dinerop.onboarding.application.usecase.CrearSolicitudConSolicitanteUseCase;
import com.markup.dinerop.onboarding.application.usecase.ObtenerOnboardingUnificadoUseCase;
import com.markup.dinerop.onboarding.dto.request.PersonaOnboardingRequest;
import com.markup.dinerop.onboarding.dto.request.SolicitudOnboardingRequest;
import com.markup.dinerop.onboarding.dto.response.OnboardingUnificadoResponse;
import com.markup.dinerop.onboarding.dto.response.SolicitudOnboardingResponse;
import com.markup.dinerop.onboarding.infrastructure.mapper.OnboardingMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/onboarding/cliente")
@RequiredArgsConstructor
public class OnboardingClienteController {

    private final CrearSolicitudConSolicitanteUseCase crearSolicitudConSolicitanteUC;
    private final ObtenerOnboardingUnificadoUseCase obtenerOnboardingUC;
    private final CompletarFormularioGaranteUseCase completarGaranteUC;
    private final OnboardingMapper mapper;

    @PostMapping("/solicitante")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENT')")
    public SolicitudOnboardingResponse crearSolicitudConSolicitante(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SolicitudOnboardingRequest request
    ) {
        log.info("[ONBOARDING] Crear solicitud solicitante | clientId={}", user.getIdUser());
        return mapper.toSolicitudResponse(
                crearSolicitudConSolicitanteUC.execute(user.getIdUser(), request)
        );
    }

    @PostMapping("/garante/completar")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENT')")
    public SolicitudOnboardingResponse completarGarante(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PersonaOnboardingRequest garanteRequest
    ) {
        log.info("[ONBOARDING] Completar garante | clientId={}", user.getIdUser());
        return mapper.toSolicitudResponse(
                completarGaranteUC.execute(user.getIdUser(), garanteRequest)
        );
    }

    @GetMapping("/{solicitudId}")
    @PreAuthorize("hasRole('CLIENT')")
    public OnboardingUnificadoResponse obtenerOnboarding(
            @PathVariable Long solicitudId
    ) {
        return mapper.toOnboardingUnificadoResponse(
                obtenerOnboardingUC.execute(solicitudId)
        );
    }
}
