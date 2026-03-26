package com.markup.dinerop.cooperative.infrastructure.web;

import com.markup.dinerop.cooperative.domain.entity.enums.CreditType;
import com.markup.dinerop.cooperative.domain.service.CooperativeService;
import com.markup.dinerop.cooperative.dto.CooperativeRateDto;
import com.markup.dinerop.cooperative.dto.InternalCooperativeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/internal/cooperatives")
@RequiredArgsConstructor
public class InternalCooperativeController {

    private final CooperativeService cooperativeService;

    @GetMapping("/by-city")
    public ResponseEntity<List<InternalCooperativeDto>> findByCity(@RequestParam String city) {
        return ResponseEntity.ok(cooperativeService.findInternalByCity(city));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternalCooperativeDto> getInternalById(@PathVariable Long id) {
        return ResponseEntity.ok(cooperativeService.getInternalById(id));
    }

    @GetMapping("/eligible")
    public ResponseEntity<List<InternalCooperativeDto>> eligible(
            @RequestParam String city,
            @RequestParam BigDecimal minAmount
    ) {
        return ResponseEntity.ok(cooperativeService.findInternalEligible(city, minAmount));
    }

    @GetMapping("/{id}/rates/{tipoCredito}")
    public ResponseEntity<CooperativeRateDto> getActiveRate(
            @PathVariable Long id,
            @PathVariable String tipoCredito
    ) {
        return ResponseEntity.ok(
                cooperativeService.getActiveRateDto(id, CreditType.valueOf(tipoCredito.toUpperCase()))
        );
    }
}
