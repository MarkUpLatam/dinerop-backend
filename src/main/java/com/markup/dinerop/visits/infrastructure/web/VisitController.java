package com.markup.dinerop.visits.infrastructure.web;

import com.markup.dinerop.visits.domain.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    /**
     * GET /api/visits
     * Retorna el total actual de visitas (sin incrementar).
     */
    @GetMapping
    public ResponseEntity<Map<String, Long>> getTotal() {
        return ResponseEntity.ok(Map.of("total", visitService.getTotal()));
    }

    /**
     * POST /api/visits
     * Registra una nueva visita y retorna el nuevo total.
     * El frontend lo llama una vez por sesión de usuario.
     */
    @PostMapping
    public ResponseEntity<Map<String, Long>> registerVisit() {
        return ResponseEntity.ok(Map.of("total", visitService.increment()));
    }
}
