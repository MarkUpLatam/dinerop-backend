package com.markup.dinerop.credit.domain.service;

import com.markup.dinerop.credit.domain.model.SolicitudCooperativa;
import com.markup.dinerop.credit.domain.model.enums.SolicitudCooperativaStatus;
import com.markup.dinerop.credit.infrastructure.repository.SolicitudCooperativaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClientCreditAcceptanceService {

    private final SolicitudCooperativaRepository solicitudCooperativaRepository;

    @Transactional
    public void acceptPreApproved(Long clientId, Long solicitudId, Long cooperativaId) {

        // 1) Cargar la fila objetivo con lock (evita carreras)
        SolicitudCooperativa sc = solicitudCooperativaRepository
                .findForClientAcceptance(clientId, solicitudId, cooperativaId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud/Cooperativa no encontrada para este cliente"));

        // 2) Validar que aÃºn NO exista una aceptada para esa solicitud
        boolean alreadyAccepted = solicitudCooperativaRepository
                .existsBySolicitudIdAndEstado(solicitudId, SolicitudCooperativaStatus.ACEPTADA);

        if (alreadyAccepted) {
            throw new IllegalStateException("Esta solicitud ya fue aceptada previamente en una entidad financiera");
        }

        // 3) Validar que la cooperativa estÃ© en PRE_APROBADA
        if (sc.getEstado() != SolicitudCooperativaStatus.PRE_APROBADA) {
            throw new IllegalStateException("Solo se puede aceptar una solicitud en estado PRE_APROBADA");
        }

        // 4) Actualizar estado + fecha
        sc.setEstado(SolicitudCooperativaStatus.ACEPTADA);
        sc.setFechaActualizacion(LocalDateTime.now());

        // 5) Guardar
        solicitudCooperativaRepository.save(sc);
    }
}

