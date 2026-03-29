package com.markup.dinerop.onboarding.infrastructure.repository;

import com.markup.dinerop.onboarding.domain.entity.IngresoEgreso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngresoEgresoRepository extends JpaRepository<IngresoEgreso, Long> {
}
