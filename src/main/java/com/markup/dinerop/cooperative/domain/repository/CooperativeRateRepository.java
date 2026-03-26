package com.markup.dinerop.cooperative.domain.repository;

import com.markup.dinerop.cooperative.domain.entity.CooperativeRate;
import com.markup.dinerop.cooperative.domain.entity.enums.CreditType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CooperativeRateRepository
        extends JpaRepository<CooperativeRate, Long> {

    Optional<CooperativeRate>
    findByCooperativaIdAndTipoCreditoAndActivaTrue(
            Long cooperativaId,
            CreditType tipoCredito
    );
}
