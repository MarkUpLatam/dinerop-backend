package com.markup.dinerop.credit.infrastructure.web;

import com.markup.dinerop.credit.domain.service.CreditService;
import com.markup.dinerop.credit.dto.ActualizarEstadoSolicitudRequest;
import com.markup.dinerop.credit.dto.InternalOnboardingCompletedRequest;
import com.markup.dinerop.credit.dto.LinkClientRequestDto;
import com.markup.dinerop.credit.dto.LinkClientResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/internal/credits")
@RequiredArgsConstructor
public class InternalCreditController {

    private final CreditService creditService;

    @PostMapping("/link-client")
    public ResponseEntity<LinkClientResponseDto> linkClient(
            @Valid @RequestBody LinkClientRequestDto request
    ) {
        log.info(
                "[INTERNAL][LINK-CLIENT] incoming | email={} | clientId={}",
                safeEmail(request.getEmail()),
                request.getClientId()
        );

        String normalizedEmail = normalizeEmail(request.getEmail());

        log.info(
                "[INTERNAL][LINK-CLIENT] linking | normalizedEmail={} | clientId={}",
                normalizedEmail,
                request.getClientId()
        );

        int updated = creditService.linkClientToRequests(normalizedEmail, request.getClientId());

        log.info(
                "[INTERNAL][LINK-CLIENT] result | normalizedEmail={} | updatedRows={}",
                normalizedEmail,
                updated
        );

        return ResponseEntity.ok(
                LinkClientResponseDto.builder()
                        .updated(updated)
                        .build()
        );
    }

    @PostMapping("/completed")
    public ResponseEntity<Void> onboardingCompleted(
            @RequestBody InternalOnboardingCompletedRequest request
    ) {
        log.info(
                "[INTERNAL][ONBOARDING-COMPLETED] incoming | clientId={}",
                request != null ? request.getClientId() : null
        );

        Long clientId = request != null ? request.getClientId() : null;
        if (clientId == null) {
            log.warn("[INTERNAL][ONBOARDING-COMPLETED] clientId is null");
            return ResponseEntity.badRequest().build();
        }

        log.info("[INTERNAL][ONBOARDING-COMPLETED] dispatching for clientId={}", clientId);

        creditService.completeOnboardingAndDispatch(clientId);

        log.info("[INTERNAL][ONBOARDING-COMPLETED] done | clientId={}", clientId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/solicitudes/{solicitudId}/estado")
    public ResponseEntity<Void> actualizarEstadoSolicitud(
            @PathVariable Long solicitudId,
            @RequestBody ActualizarEstadoSolicitudRequest request
    ) {
        creditService.actualizarEstadoSolicitudCooperativa(
                solicitudId,
                request.getCooperativaId(),
                request.getNuevoEstado()
        );

        return ResponseEntity.ok().build();
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private String safeEmail(String email) {
        return email == null ? null : email.trim();
    }
}
