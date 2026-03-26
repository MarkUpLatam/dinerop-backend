package com.markup.dinerop.credit.infrastructure.repository;

import com.markup.dinerop.credit.domain.model.SolicitudCooperativaCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolicitudCooperativaCotizacionRepository
        extends JpaRepository<SolicitudCooperativaCotizacion, Long> {

    Optional<SolicitudCooperativaCotizacion>
    findBySolicitudCooperativaId(Long solicitudCooperativaId);
}

