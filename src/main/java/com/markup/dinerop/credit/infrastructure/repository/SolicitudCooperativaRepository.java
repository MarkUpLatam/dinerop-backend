package com.markup.dinerop.credit.infrastructure.repository;

import com.markup.dinerop.credit.domain.model.SolicitudCooperativa;
import com.markup.dinerop.credit.domain.model.enums.SolicitudCooperativaStatus;
import com.markup.dinerop.credit.dto.CooperativeCreditRequestItemDto;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;



import java.util.List;
import java.util.Optional;

public interface SolicitudCooperativaRepository extends JpaRepository<SolicitudCooperativa, Long> {

    boolean existsBySolicitudIdAndCooperativaId(Long solicitudId, Long cooperativaId);

    @Query("""
    SELECT sc
    FROM SolicitudCooperativa sc
    JOIN FETCH sc.creditRequest cr
    WHERE cr.clientId = :clientId
""")
    List<SolicitudCooperativa> findAllByClientId(@Param("clientId") Long clientId);



        @Query("""
        select new com.markup.dinerop.credit.dto.CooperativeCreditRequestItemDto(
            sc.solicitudId,
            cr.amount,
            cr.tipo,
            cr.fechaSolicitud,
            sc.estado
        )
        from SolicitudCooperativa sc
        join sc.creditRequest cr
        where sc.cooperativaId = :cooperativaId
        order by cr.fechaSolicitud desc
    """)

    List<CooperativeCreditRequestItemDto> findRequestsByCooperativaId(@Param("cooperativaId") Long cooperativaId);

    Optional<SolicitudCooperativa> findBySolicitudIdAndCooperativaId(Long solicitudId, Long cooperativaId);

    //----------------------------------------------------------------------------------------------------------------
    //Metodo para obtener detalle de solicitud en diversas cooperativas
    //----------------------------------------------------------------------------------------------------------------

    @Query("""
    SELECT sc
    FROM SolicitudCooperativa sc
    JOIN FETCH sc.creditRequest cr
    WHERE cr.clientId = :clientId
      AND cr.id = :solicitudId
      AND sc.estado IN :estados
    ORDER BY sc.fechaActualizacion DESC NULLS LAST
""")
    List<SolicitudCooperativa> findByClientIdAndSolicitudIdAndEstado(
            @Param("clientId") Long clientId,
            @Param("solicitudId") Long solicitudId,
            @Param("estados") List<SolicitudCooperativaStatus> estados
    );



    //----------------------------------------------------------------------------------------------------------------
    //Metodo para aceptar una solicitud pre-aprobada con validacion incluida para no aceptar mas de una solicitud
    //----------------------------------------------------------------------------------------------------------------

    boolean existsBySolicitudIdAndEstado(Long solicitudId, SolicitudCooperativaStatus estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT sc
    FROM SolicitudCooperativa sc
    JOIN FETCH sc.creditRequest cr
    WHERE cr.clientId = :clientId
      AND cr.id = :solicitudId
      AND sc.cooperativaId = :cooperativaId
""")
    Optional<SolicitudCooperativa> findForClientAcceptance(
            @Param("clientId") Long clientId,
            @Param("solicitudId") Long solicitudId,
            @Param("cooperativaId") Long cooperativaId
    );






}

