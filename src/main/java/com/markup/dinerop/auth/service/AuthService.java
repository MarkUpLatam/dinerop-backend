package com.markup.dinerop.auth.service;

import com.markup.dinerop.auth.dto.*;

import com.markup.dinerop.auth.entity.ActivationToken;
import com.markup.dinerop.auth.entity.Role;
import com.markup.dinerop.auth.entity.User;
import com.markup.dinerop.auth.exception.AccountNotActiveException;
import com.markup.dinerop.auth.repository.ActivationTokenRepository;
import com.markup.dinerop.auth.repository.UserRepository;
import com.markup.dinerop.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;


import com.markup.dinerop.auth.entity.PasswordResetToken;
import com.markup.dinerop.auth.repository.PasswordResetTokenRepository;
import com.markup.dinerop.auth.dto.ForgotPasswordRequest;
import com.markup.dinerop.auth.dto.ResetPasswordRequest;





import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final NotificationService notificationService;



    // =========================================================
    // PRE-REGISTRO (SIN contraseÃ±a, SIN JWT)
    // =========================================================
    @Transactional
    public String preRegister(String email, Role role) {

        String normalizedEmail = email.toLowerCase().trim();

        Optional<User> existingOpt = userRepository.findByEmail(normalizedEmail);

        // =============================
        // USUARIO YA EXISTE
        // =============================
        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();

            // Ya activo â†’ no hacemos nada
            if ("ACTIVE".equals(existing.getStatus())) {
                throw new RuntimeException("USER_ALREADY_ACTIVE");
            }

            // Buscar token vÃ¡lido
            Optional<ActivationToken> tokenOpt =
                    activationTokenRepository.findByUser_IdUserAndUsedFalse(existing.getIdUser());

            if (tokenOpt.isPresent()) {
                ActivationToken token = tokenOpt.get();

                // Token aÃºn vÃ¡lido â†’ reutilizar
                if (token.getExpiresAt().isAfter(Instant.now())) {
                    //publishActivationEvent(existing, token.getToken());
                    return token.getToken();
                }

                // Token expirado â†’ marcar como usado
                token.markAsUsed();
                activationTokenRepository.save(token);
            }
            // Si no hay token o estaba expirado â†’ cae y genera uno nuevo
        }

        // =============================
        // CREAR USUARIO NUEVO
        // =============================
        User user = User.builder()
                .email(normalizedEmail)
                .role(role)
                .status("PENDING_ACTIVATION")
                .active(false)
                .build();

        log.info("Pre-register request for {}", normalizedEmail);

        User savedUser = userRepository.save(user);

        // =============================
        // GENERAR NUEVO TOKEN
        // =============================
        String tokenValue = UUID.randomUUID().toString();

        ActivationToken newToken = ActivationToken.builder()
                .token(tokenValue)
                .user(savedUser)
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .used(false)
                .build();


        activationTokenRepository.save(newToken);

        // =============================
        // Enviar correo
        // =============================


        notificationService.sendActivationEmail(savedUser.getEmail(), tokenValue);

        log.info("New activation token generated for {}", savedUser.getEmail());


        return tokenValue;
    }



    // =========================================================
    // OBTENER EMAIL POR CLIENT ID
    // =========================================================
    public String getEmailByClientId(Long clientId) {
        return userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"))
                .getEmail();
    }

    // =========================================================
    // LOGIN
    // =========================================================
    public LoginResponse login(LoginRequest request) {


        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales invalidas"));


        if (!user.isEnabled()) {
            throw new AccountNotActiveException("Cuenta no activada. Revisa tu correo.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User authenticatedUser = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(authenticatedUser);

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                authenticatedUser.getIdUser(),
                authenticatedUser.getEmail(),
                authenticatedUser.getRole().name(),
                authenticatedUser.getStatus()
        );

        return new LoginResponse(accessToken, userInfo);
    }


    // =========================================================
    // Activar Cuenta
    // =========================================================

    @Transactional
    public void activateAccount(String tokenValue) {

        ActivationToken token = activationTokenRepository
                .findByTokenAndUsedFalse(tokenValue)
                .orElseThrow(() -> new RuntimeException("INVALID_OR_USED_TOKEN"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("TOKEN_EXPIRED");
        }

        User user = token.getUser();
        user.setStatus("ACTIVE");
        user.setActive(true);

        token.markAsUsed();

        userRepository.save(user);
        activationTokenRepository.save(token);

        log.info("Cuenta activada correctamente: {}", user.getEmail());
    }


    // =========================================================
    // Completar registro
    // =========================================================

    @Transactional
    public void completeRegistration(String email, String rawPassword) {

        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("USER_NOT_ACTIVE");
        }

        if (user.getPassword() != null) {
            throw new RuntimeException("PASSWORD_ALREADY_SET");
        }

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setActive(true);

        userRepository.save(user);


    }



    // =========================================================
    // FORGOT PASSWORD
    // =========================================================
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {

        String email = request.getEmail().toLowerCase().trim();

        Optional<User> userOpt = userRepository.findByEmail(email);

        // 1. Siempre responder OK (seguridad)
        if (userOpt.isEmpty()) {
            log.warn("Password reset requested for non-existing email: {}", email);
            return;
        }

        User user = userOpt.get();

        // 2. Solo permitir reset a usuarios ACTIVE
        if (!"ACTIVE".equals(user.getStatus())) {
            log.warn("Password reset requested for non-active user: {}", email);
            return;
        }

        // 3. Invalidar tokens anteriores
        passwordResetTokenRepository
                .findAllByUser_IdUserAndUsedFalse(user.getIdUser())
                .forEach(token -> {
                    token.setUsed(true);
                    passwordResetTokenRepository.save(token);
                });

        // 4. Generar nuevo token
        String tokenValue = UUID.randomUUID().toString();

        var expiresAt = Instant.now()
                .plus(15, ChronoUnit.MINUTES)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(tokenValue)
                .user(user)
                .expiresAt(expiresAt)
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        //envio de correo
        notificationService.sendResetPasswordEmail(user.getEmail(), tokenValue);


        log.info("Password reset token generated for {}", email);
    }


    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken token = passwordResetTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("INVALID_OR_USED_TOKEN"));

        if (token.isUsed()) {
            throw new RuntimeException("TOKEN_ALREADY_USED");
        }

        if (token.getExpiresAt().isBefore(
                Instant.now()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()
        )) {
            throw new RuntimeException("TOKEN_EXPIRED");
        }

        User user = token.getUser();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setActive(true);

        token.setUsed(true);

        userRepository.save(user);
        passwordResetTokenRepository.save(token);

        log.info("Password successfully reset for {}", user.getEmail());
    }


}
