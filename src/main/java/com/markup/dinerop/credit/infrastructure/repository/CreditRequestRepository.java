package com.markup.dinerop.credit.infrastructure.repository;

import com.markup.dinerop.credit.domain.model.CreditRequest;
import com.markup.dinerop.credit.domain.model.enums.CreditRequestStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CreditRequestRepository extends JpaRepository<CreditRequest, Long> {


    //==================================================================================================
    // Para dashboard del usuario
    //==================================================================================================

    List<CreditRequest> findByClientId(Long clientId);


    //==================================================================================================
    // Para solicitudes pÃºblicas
    //==================================================================================================
    List<CreditRequest> findByEmail(String email);

    boolean existsByEmailAndEstado(String email, CreditRequestStatus estado);

    Optional<CreditRequest> findFirstByEmailAndEstadoOrderByFechaSolicitudDesc(
            String email,
            String estado
    );


    //==================================================================================================
    // ENVIAR SOLICITUDES CREADAS POR CADA CLIENTE
    //==================================================================================================

    List<CreditRequest> findByClientIdAndEstado(Long clientId, CreditRequestStatus estado);


    //==================================================================================================
    //Vincular la tabla solicitud de credito con id del cliente que creo la solicitud luego de registrarse
    //==================================================================================================
    @Modifying
    @Transactional
    @Query("""
        update CreditRequest cr
        set cr.clientId = :clientId,
            cr.fechaActualizacion = CURRENT_TIMESTAMP
        where cr.email = :email
          and cr.clientId is null
    """)
    int linkClientIdByEmail(
            @Param("email") String email,
            @Param("clientId") Long clientId
    );

    //==================================================================================================
    //Actualizar estado de solicitud
    //==================================================================================================
    @Modifying
    @Query("""
        update CreditRequest cr
        set cr.estado = :newStatus
        where cr.clientId = :clientId
          and cr.estado = :currentStatus
    """)
    int updateStatusByClientId(
            Long clientId,
            CreditRequestStatus currentStatus,
            CreditRequestStatus newStatus
    );




}

