package com.markup.dinerop.visits.domain.service;

import com.markup.dinerop.visits.domain.entity.PageVisit;
import com.markup.dinerop.visits.domain.repository.PageVisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final PageVisitRepository pageVisitRepository;

    /**
     * Retorna el total actual de visitas sin incrementar.
     * Se usa en el primer render del componente.
     */
    @Transactional
    public long getTotal() {
        return getOrCreate().getTotalVisits();
    }

    /**
     * Incrementa en 1 el contador global y retorna el nuevo total.
     * Se llama una vez por sesión de usuario.
     */
    @Transactional
    public long increment() {
        PageVisit visit = getOrCreate();
        visit.setTotalVisits(visit.getTotalVisits() + 1);
        return pageVisitRepository.save(visit).getTotalVisits();
    }

    /**
     * Obtiene el registro único o lo crea si no existe (con valor inicial 2000).
     */
    private PageVisit getOrCreate() {
        return pageVisitRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    PageVisit newVisit = new PageVisit();
                    newVisit.setTotalVisits(2000L);
                    return pageVisitRepository.save(newVisit);
                });
    }
}
