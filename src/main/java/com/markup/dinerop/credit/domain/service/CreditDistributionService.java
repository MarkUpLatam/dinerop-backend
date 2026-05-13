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
        String province = request.getProvince();

        if (request.getAmount() == null) {
            log.warn("[DISTRIBUTION] Request {} has no amount. Skipping distribution.", request.getId());
            return 0;
        }

        if ((city == null || city.isBlank()) && (province == null || province.isBlank())) {
            log.warn("[DISTRIBUTION] Request {} has no city nor province. Skipping distribution.", request.getId());
            return 0;
        }

        // 1) Intentar por ciudad + provincia (más especifico)
        List<Cooperative> cooperatives = cooperativeService.getEligibleCooperatives(
                city,
                province,
                request.getAmount().doubleValue()
        );

        // 2) Fallback: si no hay cooperativas en la ciudad, intentar a nivel provincia
        if (cooperatives.isEmpty() && province != null && !province.isBlank()) {
            log.info(
                    "[DISTRIBUTION] No cooperatives in city={}. Falling back to province={} | requestId={}",
                    city, province, request.getId()
            );
            cooperatives = cooperativeService.getEligibleCooperatives(
                    null,
                    province,
                    request.getAmount().doubleValue()
            );
        }

        if (cooperatives.isEmpty()) {
            log.info(
                    "[DISTRIBUTION] No eligible cooperatives even after fallback | city={} province={} amount={} requestId={}",
                    city,
                    province,
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
