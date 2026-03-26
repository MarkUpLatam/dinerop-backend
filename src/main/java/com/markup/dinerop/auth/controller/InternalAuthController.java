package com.markup.dinerop.auth.controller;

import com.markup.dinerop.auth.dto.PreRegisterRequest;
import com.markup.dinerop.auth.dto.PreRegisterResponse;
import com.markup.dinerop.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/auth")
@RequiredArgsConstructor
public class InternalAuthController {

    private final AuthService authService;

    @PostMapping("/pre-register")
    public ResponseEntity<PreRegisterResponse> preRegister(
            @RequestBody PreRegisterRequest request
    ) {
        String token = authService.preRegister(
                request.getEmail(),
                request.getRole()
        );

        return ResponseEntity.ok(new PreRegisterResponse(token));
    }
}
