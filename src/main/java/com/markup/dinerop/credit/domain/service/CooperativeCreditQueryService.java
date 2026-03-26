package com.markup.dinerop.credit.domain.service;

import com.markup.dinerop.credit.dto.CooperativeCreditRequestItemDto;
import com.markup.dinerop.credit.infrastructure.repository.SolicitudCooperativaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CooperativeCreditQueryService {

    private final SolicitudCooperativaRepository solicitudCooperativaRepository;

    public List<CooperativeCreditRequestItemDto> getRequestsForCooperative(Long cooperativaId) {
        return solicitudCooperativaRepository.findRequestsByCooperativaId(cooperativaId);
    }
}

