package com.markup.dinerop.credit.domain.service;

import com.markup.dinerop.cooperative.domain.service.CooperativeService;
import com.markup.dinerop.credit.domain.model.CreditRequest;
import com.markup.dinerop.credit.domain.model.SolicitudCooperativa;
import com.markup.dinerop.credit.domain.model.SolicitudCooperativaCotizacion;
import com.markup.dinerop.credit.domain.model.enums.SolicitudCooperativaStatus;
import com.markup.dinerop.credit.domain.service.calculation.LoanCalculator;
import com.markup.dinerop.credit.infrastructure.repository.SolicitudCooperativaCotizacionRepository;
import com.markup.dinerop.credit.infrastructure.repository.SolicitudCooperativaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CooperativeCreditDecisionService {

    private final SolicitudCooperativaRepository repository;
    private final CooperativeService cooperativeService;
    private final SolicitudCooperativaCotizacionRepository cotizacionRepository;

    @Transactional
    public void decide(
            Long solicitudId,
            Long cooperativaId,
            String decision
    ) {

        SolicitudCooperativa sc = repository
                .findBySolicitudIdAndCooperativaId(solicitudId, cooperativaId)
                .orElseThrow(() ->
                        new AccessDeniedException("Solicitud no pertenece a esta cooperativa")
                );

        if (sc.getEstado() != SolicitudCooperativaStatus.ENVIADA) {
            throw new IllegalStateException("La solicitud ya fue procesada");
        }

        CreditRequest cr = sc.getCreditRequest();
        if (cr == null) {
            throw new IllegalStateException("CreditRequest no cargado");
        }

        if ("PRE_APROBAR".equalsIgnoreCase(decision)) {

            Double rate = cooperativeService.getActiveRate(cooperativaId, cr.getCreditType());

            if (cr.getCreditType() == null) {
                throw new IllegalStateException("Tipo de credito no definido");
            }
            if (cr.getAmount() == null || cr.getAmount().signum() <= 0) {
                throw new IllegalStateException("Monto invalido");
            }
            if (cr.getPlazoMeses() == null || cr.getPlazoMeses() <= 0) {
                throw new IllegalStateException("Plazo invalido");
            }

            var calc = LoanCalculator.calculate(
                    cr.getAmount(),
                    BigDecimal.valueOf(rate),
                    cr.getPlazoMeses()
            );

            var cotizacion = cotizacionRepository
                    .findBySolicitudCooperativaId(sc.getId())
                    .orElseGet(SolicitudCooperativaCotizacion::new);

            cotizacion.setSolicitudCooperativaId(sc.getId());
            cotizacion.setMonto(cr.getAmount());
            cotizacion.setPlazoMeses(cr.getPlazoMeses());
            cotizacion.setTipoCredito(cr.getCreditType());
            cotizacion.setTasaAnual(BigDecimal.valueOf(rate));
            cotizacion.setTasaMensual(calc.tasaMensual());
            cotizacion.setCuotaMensual(calc.cuotaMensual());
            cotizacion.setTotalPagar(calc.totalPagar());
            cotizacion.setInteresTotal(calc.interesTotal());

            cotizacionRepository.save(cotizacion);

            sc.setEstado(SolicitudCooperativaStatus.PRE_APROBADA);
            repository.save(sc);

        } else if ("RECHAZAR".equalsIgnoreCase(decision)) {

            sc.setEstado(SolicitudCooperativaStatus.RECHAZADA);
            repository.save(sc);

        } else {
            throw new IllegalArgumentException("Decision invalida");
        }
    }
}
