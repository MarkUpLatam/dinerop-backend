package com.markup.dinerop.onboarding.application.service.impl;

import com.markup.dinerop.credit.domain.service.CreditService;
import com.markup.dinerop.onboarding.application.service.OnboardingService;
import com.markup.dinerop.onboarding.domain.entity.*;
import com.markup.dinerop.onboarding.domain.enums.EstadoFormulario;
import com.markup.dinerop.onboarding.domain.enums.EstadoOnboarding;
import com.markup.dinerop.onboarding.domain.enums.RolPersona;
import com.markup.dinerop.onboarding.dto.request.PersonaOnboardingRequest;
import com.markup.dinerop.onboarding.dto.request.SolicitudOnboardingRequest;
import com.markup.dinerop.onboarding.exception.BusinessException;
import com.markup.dinerop.onboarding.exception.ResourceNotFoundException;
import com.markup.dinerop.onboarding.infrastructure.mapper.OnboardingMapper;
import com.markup.dinerop.onboarding.infrastructure.repository.PersonaOnboardingRepository;
import com.markup.dinerop.onboarding.infrastructure.repository.SolicitudOnboardingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingServiceImpl implements OnboardingService {

    private final SolicitudOnboardingRepository solicitudRepo;
    private final PersonaOnboardingRepository personaRepo;
    private final CreditService creditService;
    private final OnboardingMapper mapper;

    @Override
    public SolicitudOnboarding crearSolicitudConSolicitante(
            Long clientId,
            SolicitudOnboardingRequest request
    ) {
        if (solicitudRepo.existsByUsuarioId(clientId)) {
            throw new BusinessException("Este usuario ya ha completado su formulario");
        }

        SolicitudOnboarding solicitud = solicitudRepo.save(
                SolicitudOnboarding.builder()
                        .usuarioId(clientId)
                        .destinoCredito(request.getDestinoCredito())
                        .estado(EstadoOnboarding.EN_PROCESO)
                        .build()
        );

        if (solicitud.getPersonas() == null) {
            solicitud.setPersonas(new java.util.ArrayList<>());
        }

        PersonaOnboarding solicitante = mapper.toPersona(request.getSolicitante());
        solicitante.setSolicitud(solicitud);
        solicitante.setRol(RolPersona.SOLICITANTE);
        solicitante.setEstadoFormulario(EstadoFormulario.COMPLETO);
        linkPersonaRelations(solicitante, request.getSolicitante());
        solicitud.getPersonas().add(solicitante);
        personaRepo.save(solicitante);

        if (request.getSolicitante().getTieneConyuge()) {
            if (request.getConyuge() == null) {
                throw new BusinessException("Debe enviar la información del cónyuge del solicitante");
            }
            PersonaOnboarding conyuge = mapper.toPersona(request.getConyuge());
            conyuge.setSolicitud(solicitud);
            conyuge.setRol(RolPersona.CONYUGE_SOLICITANTE);
            conyuge.setEstadoFormulario(EstadoFormulario.COMPLETO);
            linkPersonaRelations(conyuge, request.getConyuge());
            solicitud.getPersonas().add(conyuge);
            personaRepo.save(conyuge);
        }

        solicitud.setEstado(EstadoOnboarding.COMPLETADO);

        // Llamada directa al CreditService del monolito
        creditService.completeOnboardingAndDispatch(clientId);

        return solicitud;
    }

    @Override
    public SolicitudOnboarding solicitarGarante(Long solicitudId, Long cooperativaId) {
        SolicitudOnboarding solicitud = solicitudRepo.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        if (solicitud.getEstado() == EstadoOnboarding.SOLICITANDO_GARANTE) {
            return solicitud;
        }

        if (solicitud.getEstado() != EstadoOnboarding.COMPLETADO) {
            throw new BusinessException("No se puede solicitar garante en el estado actual");
        }

        solicitud.setEstado(EstadoOnboarding.SOLICITANDO_GARANTE);

        // Llamada directa al CreditService del monolito
        creditService.solicitarGarante(solicitudId, cooperativaId);

        return solicitud;
    }

    @Override
    public SolicitudOnboarding crearYCompletarGaranteDesdeCliente(
            Long clientId,
            PersonaOnboardingRequest request
    ) {
        SolicitudOnboarding solicitud = solicitudRepo
                .findByUsuarioId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada para este usuario"));

        if (personaRepo.existsBySolicitudIdAndRol(solicitud.getId(), RolPersona.GARANTE)) {
            throw new BusinessException("El garante ya fue registrado");
        }

        PersonaOnboarding garante = mapper.toPersona(request);
        garante.setSolicitud(solicitud);
        garante.setRol(RolPersona.GARANTE);
        garante.setEstadoFormulario(EstadoFormulario.COMPLETO);
        linkPersonaRelations(garante, request);
        solicitud.getPersonas().add(garante);
        personaRepo.save(garante);

        boolean solicitanteCompleto = personaRepo.existsBySolicitudIdAndRolAndEstadoFormulario(
                solicitud.getId(), RolPersona.SOLICITANTE, EstadoFormulario.COMPLETO
        );

        if (solicitanteCompleto) {
            solicitud.setEstado(EstadoOnboarding.COMPLETADO);
        }

        return solicitud;
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitudOnboarding obtenerOnboardingUnificado(Long solicitudId) {
        return solicitudRepo.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
    }

    private void linkPersonaRelations(PersonaOnboarding persona, PersonaOnboardingRequest request) {
        DireccionOnboarding direccion = mapper.toDireccion(request.getDireccion());
        direccion.setPersona(persona);
        persona.setDireccion(direccion);

        if (request.getActividadEconomica() != null) {
            ActividadEconomica actividad = mapper.toActividadEconomica(request.getActividadEconomica());
            actividad.setPersona(persona);
            persona.setActividadEconomica(actividad);
        }

        IngresoEgreso ingresoEgreso = mapper.toIngresoEgreso(request.getIngresoEgreso());
        ingresoEgreso.setPersona(persona);
        persona.setIngresoEgreso(ingresoEgreso);

        if (request.getReferencias() != null && !request.getReferencias().isEmpty()) {
            List<ReferenciaPersonal> referencias = mapper.toReferencias(request.getReferencias());
            referencias.forEach(r -> r.setPersona(persona));
            persona.setReferencias(referencias);
        }
    }
}
