package com.markup.dinerop.credit.domain.service;

import com.markup.dinerop.credit.domain.model.SolicitudCooperativa;
import com.markup.dinerop.credit.dto.ClientCreditResponseDto;
import com.markup.dinerop.credit.infrastructure.repository.SolicitudCooperativaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.markup.dinerop.credit.domain.model.enums.SolicitudCooperativaStatus;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientCreditQueryService {

    private final SolicitudCooperativaRepository solicitudCooperativaRepository;

    public ClientCreditResponseDto getMyCredits(Long clientId) {

        List<SolicitudCooperativa> all = solicitudCooperativaRepository.findAllByClientId(clientId);

        if (all.isEmpty()) {
            return ClientCreditResponseDto.builder()
                    .cantidadSolicitudesEnviadas(0)
                    .build();
        }

        // Tomamos la solicitud (CreditRequest) mÃ¡s reciente
        SolicitudCooperativa latestSc = all.stream()
                .max(Comparator
                        .comparing((SolicitudCooperativa sc) -> sc.getCreditRequest().getFechaSolicitud())
                        .thenComparing(sc -> sc.getCreditRequest().getId())
                )
                .orElseThrow();

        var latestCr = latestSc.getCreditRequest();

        // Solo envÃ­os asociados a esa solicitud
        List<SolicitudCooperativa> forLatest = all.stream()
                .filter(sc -> sc.getCreditRequest().getId().equals(latestCr.getId()))
                .toList();

        int count = forLatest.size();

        // Estado agregado segÃºn tu regla
        boolean anyPreApproved = forLatest.stream()
                .anyMatch(sc -> sc.getEstado() == SolicitudCooperativaStatus.PRE_APROBADA);

        boolean allRejected = forLatest.stream()
                .allMatch(sc -> sc.getEstado() == SolicitudCooperativaStatus.RECHAZADA);

        String estadoFinal;
        if (anyPreApproved) {
            estadoFinal = SolicitudCooperativaStatus.PRE_APROBADA.name();
        } else if (allRejected) {
            estadoFinal = SolicitudCooperativaStatus.RECHAZADA.name();
        } else {
            estadoFinal = SolicitudCooperativaStatus.ENVIADA.name();
        }

        return ClientCreditResponseDto.builder()
                .solicitudId(latestCr.getId())
                .estado(estadoFinal)
                .monto(latestCr.getAmount())
                .tipo(latestCr.getTipo().name())
                .fechaSolicitud(latestCr.getFechaSolicitud())
                .cantidadSolicitudesEnviadas(count)
                .build();
    }
}

