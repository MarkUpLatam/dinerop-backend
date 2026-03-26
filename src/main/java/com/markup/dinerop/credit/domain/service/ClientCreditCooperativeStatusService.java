package com.markup.dinerop.credit.domain.service;

import com.markup.dinerop.cooperative.domain.service.CooperativeService;
import com.markup.dinerop.credit.domain.model.enums.SolicitudCooperativaStatus;
import com.markup.dinerop.credit.dto.CooperativeStatusItemDto;
import com.markup.dinerop.credit.infrastructure.repository.SolicitudCooperativaCotizacionRepository;
import com.markup.dinerop.credit.infrastructure.repository.SolicitudCooperativaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientCreditCooperativeStatusService {

    private final SolicitudCooperativaRepository solicitudCooperativaRepository;
    private final SolicitudCooperativaCotizacionRepository cotizacionRepository;
    private final CooperativeService cooperativeService;

    public List<CooperativeStatusItemDto> getPreApprovedCooperatives(Long clientId, Long solicitudId) {

        var rows = solicitudCooperativaRepository.findByClientIdAndSolicitudIdAndEstado(
                clientId,
                solicitudId,
                List.of(
                        SolicitudCooperativaStatus.PRE_APROBADA,
                        SolicitudCooperativaStatus.ACEPTADA,
                        SolicitudCooperativaStatus.SOLICITANDO_GARANTE
                )
        );

        return rows.stream().map(sc -> {
            var coop = cooperativeService.getById(sc.getCooperativaId());
            var cotizacionOpt = cotizacionRepository.findBySolicitudCooperativaId(sc.getId());

            return CooperativeStatusItemDto.builder()
                    .cooperativaId(sc.getCooperativaId())
                    .nombreCooperativa(coop != null ? coop.getNombre() : "-")
                    .estado(sc.getEstado().name())
                    .fechaActualizacion(sc.getFechaActualizacion())
                    .monto(cotizacionOpt.map(c -> c.getMonto()).orElse(null))
                    .plazoMeses(cotizacionOpt.map(c -> c.getPlazoMeses()).orElse(null))
                    .tipoCredito(cotizacionOpt.map(c -> c.getTipoCredito().name()).orElse(null))
                    .tasaAnual(cotizacionOpt.map(c -> c.getTasaAnual()).orElse(null))
                    .cuotaMensual(cotizacionOpt.map(c -> c.getCuotaMensual()).orElse(null))
                    .totalPagar(cotizacionOpt.map(c -> c.getTotalPagar()).orElse(null))
                    .interesTotal(cotizacionOpt.map(c -> c.getInteresTotal()).orElse(null))
                    .build();

        }).toList();
    }
}
