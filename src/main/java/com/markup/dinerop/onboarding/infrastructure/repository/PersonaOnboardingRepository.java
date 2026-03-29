package com.markup.dinerop.onboarding.infrastructure.repository;

import com.markup.dinerop.onboarding.domain.entity.PersonaOnboarding;
import com.markup.dinerop.onboarding.domain.enums.EstadoFormulario;
import com.markup.dinerop.onboarding.domain.enums.RolPersona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonaOnboardingRepository extends JpaRepository<PersonaOnboarding, Long> {

    Optional<PersonaOnboarding> findBySolicitudIdAndRol(Long solicitudId, RolPersona rol);

    List<PersonaOnboarding> findAllBySolicitudId(Long solicitudId);

    boolean existsBySolicitudIdAndRol(Long solicitudId, RolPersona rol);

    boolean existsBySolicitudIdAndRolAndEstadoFormulario(
            Long solicitudId,
            RolPersona rol,
            EstadoFormulario estadoFormulario
    );
}
