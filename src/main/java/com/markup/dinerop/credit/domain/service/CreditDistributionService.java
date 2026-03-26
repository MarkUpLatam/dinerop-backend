package com.markup.dinerop.credit.domain.service;

import com.markup.dinerop.cooperative.domain.entity.Cooperative;
import com.markup.dinerop.cooperative.domain.service.CooperativeService;
import com.markup.dinerop.credit.domain.model.CreditRequest;
import com.markup.dinerop.credit.domain.model.SolicitudCooperativa;
import com.markup.dinerop.credit.domain.model.enums.SolicitudCooperativaStatus;
import com.markup.dinerop.credit.infrastructure.repository.SolicitudCooperativaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditDistributionService {

    private final CooperativeService cooperativeService;
    private final SolicitudCooperativaRepository solicitudCooperativaRepository;

    @Transactional
    public int distributeToCityCooperatives(CreditRequest request) {

        String city = request.getCity();
        if (city == null || city.isBlank()) {
            log.warn("[DISTRIBUTION] Request {} has no city. Skipping distribution.", request.getId());
            return 0;
        }

        if (request.getAmount() == null) {
            log.warn("[DISTRIBUTION] Request {} has no amount. Skipping distribution.", request.getId());
            return 0;
        }

        List<Cooperative> cooperatives = cooperativeService.getEligibleCooperatives(
                city,
                request.getProvince(),
                request.getAmount().doubleValue()
        );
        if (cooperatives.isEmpty()) {
            log.info(
                    "[DISTRIBUTION] No eligible cooperatives | city={} amount={} requestId={}",
                    city,
                    request.getAmount(),
                    request.getId()
            );
            return 0;
        }

        int created = 0;

        for (Cooperative cooperative : cooperatives) {
            Long coopId = cooperative.getId();

            if (solicitudCooperativaRepository.existsBySolicitudIdAndCooperativaId(request.getId(), coopId)) {
                continue;
            }

            try {
                solicitudCooperativaRepository.save(
                        SolicitudCooperativa.builder()
                                .solicitudId(request.getId())
                                .cooperativaId(coopId)
                                .estado(SolicitudCooperativaStatus.ENVIADA)
                                .build()
                );
                created++;
            } catch (Exception ex) {
                log.error(
                        "[DISTRIBUTION] Failed saving distribution requestId={} coopId={} reason={}",
                        request.getId(),
                        coopId,
                        ex.getMessage()
                );
            }
        }

        log.info("[DISTRIBUTION] Distribution done requestId={} city={} created={}", request.getId(), city, created);
        return created;
    }
}
