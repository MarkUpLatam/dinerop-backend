package com.markup.dinerop.credit.domain.service;

import com.markup.dinerop.credit.domain.model.CreditRequest;
import com.markup.dinerop.credit.domain.model.SolicitudCooperativa;
import com.markup.dinerop.credit.domain.model.enums.SolicitudCooperativaStatus;
import com.markup.dinerop.credit.dto.ClientCreditResponseDto;
import com.markup.dinerop.credit.infrastructure.repository.CreditRequestRepository;
import com.markup.dinerop.credit.infrastructure.repository.SolicitudCooperativaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientCreditQueryService {

    private final SolicitudCooperativaRepository solicitudCooperativaRepository;
    private final CreditRequestRepository creditRequestRepository;

    /**
     * Devuelve TODAS las solicitudes del cliente (incluso las que no tienen
     * distribución aún), con estado agregado calculado a partir de las
     * cooperativas a las que fueron enviadas.
     */
    public List<ClientCreditResponseDto> getMyCredits(Long clientId) {

        // 1. Todas las solicitudes del cliente, mas nuevas primero
        List<CreditRequest> requests = creditRequestRepository.findByClientId(clientId);
        if (requests.isEmpty()) {
            return List.of();
        }

        // 2. Todas las distribuciones del cliente, agrupadas por solicitudId
        Map<Long, List<SolicitudCooperativa>> distribucionesPorSolicitud =
                solicitudCooperativaRepository.findAllByClientId(clientId)
                        .stream()
                        .collect(Collectors.groupingBy(SolicitudCooperativa::getSolicitudId));

        // 3. Construir un DTO por cada solicitud
        return requests.stream()
                .sorted(Comparator
                        .comparing(CreditRequest::getFechaSolicitud).reversed()
                        .thenComparing(Comparator.comparing(CreditRequest::getId).reversed()))
                .map(cr -> toDto(cr, distribucionesPorSolicitud.getOrDefault(cr.getId(), List.of())))
                .toList();
    }

    private ClientCreditResponseDto toDto(CreditRequest cr, List<SolicitudCooperativa> distribuciones) {
        int cantidad = distribuciones.size();
        String estado = calcularEstadoAgregado(cr, distribuciones);

        return ClientCreditResponseDto.builder()
                .solicitudId(cr.getId())
                .estado(estado)
                .monto(cr.getAmount())
                .tipo(cr.getTipo().name())
                .fechaSolicitud(cr.getFechaSolicitud())
                .cantidadSolicitudesEnviadas(cantidad)
                .build();
    }

    private String calcularEstadoAgregado(CreditRequest cr, List<SolicitudCooperativa> distribuciones) {
        // Sin distribuciones aún: usar el estado bruto de solicitud_credito (CREADA / ENVIADA)
        if (distribuciones.isEmpty()) {
            return cr.getEstado().name();
        }

        boolean anyPreApproved = distribuciones.stream()
                .anyMatch(sc -> sc.getEstado() == SolicitudCooperativaStatus.PRE_APROBADA);
        if (anyPreApproved) {
            return SolicitudCooperativaStatus.PRE_APROBADA.name();
        }

        boolean anySolicitandoGarante = distribuciones.stream()
                .anyMatch(sc -> sc.getEstado() == SolicitudCooperativaStatus.SOLICITANDO_GARANTE);
        if (anySolicitandoGarante) {
            return SolicitudCooperativaStatus.SOLICITANDO_GARANTE.name();
        }

        boolean allRejected = distribuciones.stream()
                .allMatch(sc -> sc.getEstado() == SolicitudCooperativaStatus.RECHAZADA);
        if (allRejected) {
            return SolicitudCooperativaStatus.RECHAZADA.name();
        }

        return SolicitudCooperativaStatus.ENVIADA.name();
    }
}

