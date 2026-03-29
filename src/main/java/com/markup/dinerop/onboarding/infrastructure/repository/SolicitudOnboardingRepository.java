package com.markup.dinerop.onboarding.infrastructure.repository;

import com.markup.dinerop.onboarding.domain.entity.SolicitudOnboarding;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolicitudOnboardingRepository extends JpaRepository<SolicitudOnboarding, Long> {

    @EntityGraph(attributePaths = {
            "personas",
            "personas.direccion",
            "personas.actividadEconomica",
            "personas.ingresoEgreso",
            "personas.referencias"
    })
    Optional<SolicitudOnboarding> findById(Long id);

    boolean existsByUsuarioId(Long usuarioId);

    Optional<SolicitudOnboarding> findByUsuarioId(Long usuarioId);
}
