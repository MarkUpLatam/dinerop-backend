package com.markup.dinerop.credit.domain.service;

import com.markup.dinerop.auth.entity.Role;
import com.markup.dinerop.auth.service.AuthService;
import com.markup.dinerop.credit.domain.model.CreditRequest;
import com.markup.dinerop.credit.domain.model.SolicitudCooperativa;
import com.markup.dinerop.credit.domain.model.enums.CreditRequestStatus;
import com.markup.dinerop.credit.domain.model.enums.CreditRequestType;
import com.markup.dinerop.credit.domain.model.enums.SolicitudCooperativaStatus;
import com.markup.dinerop.credit.dto.ClientCreditRequestDto;
import com.markup.dinerop.credit.dto.PublicCreditRequestDto;
import com.markup.dinerop.credit.infrastructure.repository.CreditRequestRepository;
import com.markup.dinerop.credit.infrastructure.repository.SolicitudCooperativaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;





import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRequestRepository creditRequestRepository;
    private final AuthService authService;
    private final CreditDistributionService creditDistributionService;
    private final SolicitudCooperativaRepository solicitudCooperativaRepository;



    // =========================
    // Solicitud publica loging dinerup
    // =========================

    @Transactional
    public Long createPublicRequest(PublicCreditRequestDto dto) {

        // 1. Validar que no exista solicitud activa
        boolean exists =
                creditRequestRepository.existsByEmailAndEstado(
                        dto.getEmail(),
                        CreditRequestStatus.CREADA
                );

        if (exists) {
            throw new IllegalStateException("EXISTING_ACTIVE_REQUEST");
        }

        // 2. Validaciones de dominio
        if (dto.getType() == CreditRequestType.CREDITO && dto.getCreditType() == null) {
            throw new IllegalArgumentException("CREDIT_TYPE_REQUIRED_FOR_CREDITO");
        }

        if (dto.getType() == CreditRequestType.INVERSION && dto.getCreditType() != null) {
            throw new IllegalArgumentException("CREDIT_TYPE_NOT_ALLOWED_FOR_INVERSION");
        }

        if (dto.getPlazoMeses() != null &&
                (dto.getPlazoMeses() < 1 || dto.getPlazoMeses() > 360)) {
            throw new IllegalArgumentException("INVALID_PLAZO_MESES");
        }

        // 3. Persistir solicitud_credito (SOLO campos de la tabla)
        CreditRequest request = CreditRequest.builder()
                .email(dto.getEmail())
                .identification(dto.getIdentification())
                .amount(dto.getAmount())
                .province(dto.getProvince())
                .city(dto.getCity())
                .tipo(dto.getType())
                .creditType(
                        dto.getType() == CreditRequestType.CREDITO
                                ? dto.getCreditType()
                                : null
                )
                .plazoMeses(dto.getPlazoMeses())
                .estado(CreditRequestStatus.CREADA)
                .build();

        creditRequestRepository.save(request);

        // 4. Pre-registro en auth-service (datos personales/contacto)
        authService.preRegister(dto.getEmail(), Role.CLIENT);


        return request.getId();
    }


    // =========================
    // Solicitud desde cliente autenticado
    // =========================

    @Transactional
    public Long createAuthenticatedRequest(ClientCreditRequestDto dto, Long clientId, String email) {

        // Validaciones de dominio
        if (dto.getType() == CreditRequestType.CREDITO && dto.getCreditType() == null) {
            throw new IllegalArgumentException("CREDIT_TYPE_REQUIRED_FOR_CREDITO");
        }

        if (dto.getType() == CreditRequestType.INVERSION && dto.getCreditType() != null) {
            throw new IllegalArgumentException("CREDIT_TYPE_NOT_ALLOWED_FOR_INVERSION");
        }

        CreditRequest request = CreditRequest.builder()
                .clientId(clientId)
                .email(email)
                .identification("")
                .amount(dto.getMonto())
                .tipo(dto.getType())
                .creditType(
                        dto.getType() == CreditRequestType.CREDITO
                                ? dto.getCreditType()
                                : null
                )
                .plazoMeses(dto.getPlazoMeses())
                .estado(CreditRequestStatus.CREADA)
                .build();

        creditRequestRepository.save(request);
        return request.getId();
    }

    // =========================
    // Vincular solicitud con cliente.
    // =========================


    @Transactional
    public int linkClientToRequests(String email, Long clientId) {
        return creditRequestRepository.linkClientIdByEmail(email, clientId);
    }


    // ===================================================================
    // ACTUALIZAR ESTADO DE SOLICITUD LUEGO DE ONBOARDING COMPLETO
    // ===================================================================

    @Transactional
    public int markRequestsAsSent(Long clientId) {

        log.info(
                "[CREDIT] Marking requests as ENVIADA | clientId={}",
                clientId
        );

        int updated = creditRequestRepository.updateStatusByClientId(
                clientId,
                CreditRequestStatus.CREADA,
                CreditRequestStatus.ENVIADA
        );

        log.info(
                "[CREDIT] Requests updated | clientId={} count={}",
                clientId,
                updated
        );

        return updated;
    }


    // =========================
    // Listado por cliente
    // =========================

    public List<CreditRequest> getRequestsByClient(Long clientId) {
        return creditRequestRepository.findByClientId(clientId);
    }



    // =========================
    // ENVIO DE SOLICITUDES A COOPERATIVAS POR CLIENTE
    // =========================

    @Transactional
    public int completeOnboardingAndDispatch(Long clientId) {

        // Vincular email → clientId antes de buscar solicitudes
        // (necesario cuando la solicitud fue creada de forma pública sin clientId)
        creditRequestRepository.linkClientIdByEmail(
                authService.getEmailByClientId(clientId),
                clientId
        );

        List<CreditRequest> createdRequests =
                creditRequestRepository.findByClientIdAndEstado(clientId, CreditRequestStatus.CREADA);

        if (createdRequests.isEmpty()) {
            log.info("[CREDIT] No CREADA requests to send | clientId={}", clientId);
            return 0;
        }

        int totalDistributions = 0;

        for (CreditRequest req : createdRequests) {

            int distributed =
                    creditDistributionService.distributeToCityCooperatives(req);

            if (distributed > 0) {
                req.setEstado(CreditRequestStatus.ENVIADA);
                creditRequestRepository.save(req);
                totalDistributions += distributed;

                log.info(
                        "[CREDIT] Request {} marked as ENVIADA (distributed={})",
                        req.getId(), distributed
                );
            } else {
                log.warn(
                        "[CREDIT] Request {} NOT sent (no eligible cooperatives)",
                        req.getId()
                );
                // se queda en CREADA
            }
        }

        log.info("[CREDIT] Onboarding completion processed | clientId={} requests={} distributions={}",
                clientId, createdRequests.size(), totalDistributions);

        return totalDistributions;
    }
    // =========================
    // Actualizar solicitud a solicitando garante por refactorizar
    // =========================

    @Transactional
    public void actualizarEstadoSolicitudCooperativa(
            Long solicitudId,
            Long cooperativaId,
            SolicitudCooperativaStatus nuevoEstado
    ) {
        SolicitudCooperativa sc = solicitudCooperativaRepository
                .findBySolicitudIdAndCooperativaId(solicitudId, cooperativaId)
                .orElseThrow(() ->
                        new IllegalStateException("Solicitud cooperativa no encontrada")
                );

        // Validar transiciÃ³n mÃ­nima
        if (sc.getEstado() != SolicitudCooperativaStatus.ENVIADA) {
            throw new IllegalStateException(
                    "No se puede solicitar garante desde el estado " + sc.getEstado()
            );
        }

        sc.setEstado(nuevoEstado);
        // fechaActualizacion se setea por @PreUpdate
    }

    // =========================
    // Actualizar solicitud a solicitando garante nueva funcionalidad solo desde credit service
    // =========================
    @Transactional
    public void solicitarGarante(
            Long solicitudId,
            Long cooperativaId
    ) {
        SolicitudCooperativa sc = solicitudCooperativaRepository
                .findBySolicitudIdAndCooperativaId(solicitudId, cooperativaId)
                .orElseThrow(() ->
                        new IllegalStateException("Solicitud cooperativa no encontrada")
                );

        // Idempotencia
        if (sc.getEstado() == SolicitudCooperativaStatus.SOLICITANDO_GARANTE) {
            return;
        }

        // Validar transiciÃ³n
        if (sc.getEstado() != SolicitudCooperativaStatus.ENVIADA) {
            throw new IllegalStateException(
                    "No se puede solicitar garante desde el estado " + sc.getEstado()
            );
        }

        sc.setEstado(SolicitudCooperativaStatus.SOLICITANDO_GARANTE);
    }




}

