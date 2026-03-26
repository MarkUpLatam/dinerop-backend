package com.markup.dinerop.cooperative.infrastructure.web;

import com.markup.dinerop.cooperative.domain.service.CooperativeService;
import com.markup.dinerop.cooperative.dto.CreateCooperativeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cooperatives")
@RequiredArgsConstructor
public class CooperativeController {

    private final CooperativeService cooperativeService;

    @GetMapping
    public ResponseEntity<?> getAllCooperatives() {
        return ResponseEntity.ok(cooperativeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cooperativeService.getCooperativeDtoById(id));
    }

    @GetMapping("/by-city")
    public ResponseEntity<?> findByCity(@RequestParam String city) {
        return ResponseEntity.ok(cooperativeService.findByCity(city));
    }

    @GetMapping("/by-province")
    public ResponseEntity<?> findByProvince(@RequestParam String province) {
        return ResponseEntity.ok(cooperativeService.findByProvince(province));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateCooperativeDto dto) {
        return ResponseEntity.ok(cooperativeService.create(dto));
    }
}
