package com.markup.dinerop.auth.controller;

import com.markup.dinerop.auth.dto.*;
import com.markup.dinerop.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // =========================================================
    // LOGIN
    // =========================================================
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("Login: {}", request.getEmail());

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // ACTIVACIÃ“N DE CUENTA (LINK EMAIL)
    // =========================================================
    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam("token") String token) {
        log.info("[ACTIVATE] tokenPrefix={}", token.substring(0, Math.min(10, token.length())));
        authService.activateAccount(token);
        return ResponseEntity.ok("Cuenta activada correctamente");
    }


    // =========================================================
    // COMPLETE REGISTER
    // =========================================================

    @PostMapping("/complete-registration")
    public ResponseEntity<?> completeRegistration(
            @Valid @RequestBody CompleteRegistrationRequest request
    ) {
        authService.completeRegistration(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok("Registro completado correctamente");
    }

    // =========================================================
    // FORGOT PASSWORD
    // =========================================================
    @PostMapping("/password/forgot")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        authService.forgotPassword(request);

        // Siempre mismo mensaje (seguridad)
        return ResponseEntity.ok("Si el correo existe, se enviaron instrucciones");
    }

    // =========================================================
    // RESET PASSWORD
    // =========================================================
    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);

        return ResponseEntity.ok("ContraseÃ±a actualizada");
    }



}
