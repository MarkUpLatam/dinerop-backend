package com.markup.dinerop.cooperative.domain.repository;

import com.markup.dinerop.cooperative.domain.entity.Cooperative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;


@Repository
public interface CooperativeRepository extends JpaRepository<Cooperative, Long> {

    List<Cooperative> findByCiudadIgnoreCase(String ciudad);

    List<Cooperative> findByProvinciaIgnoreCase(String provincia);

    List<Cooperative> findByCiudadIgnoreCaseAndMontoMaximoCreditoGreaterThanEqual(
            String ciudad,
            BigDecimal montoMaximo
    );

    List<Cooperative> findByProvinciaIgnoreCaseAndMontoMaximoCreditoGreaterThanEqual(
            String provincia,
            BigDecimal montoMaximo
    );

    List<Cooperative> findByCiudadIgnoreCaseAndProvinciaIgnoreCaseAndMontoMaximoCreditoGreaterThanEqual(
            String ciudad,
            String provincia,
            BigDecimal montoMaximo
    );
}
