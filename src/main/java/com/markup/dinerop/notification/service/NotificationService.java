package com.markup.dinerop.notification.service;

import com.markup.dinerop.notification.dto.SendEmailDto;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Resend resend;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // =========================================================
    // GENERIC
    // =========================================================
    public void sendEmail(SendEmailDto dto) {
        try {
            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(dto.getTo())
                    .subject(dto.getSubject())
                    .html(dto.getHtmlContent())
                    .build();

            resend.emails().send(options);
            log.info("Email sent to {}: {}", dto.getTo(), dto.getSubject());
        } catch (ResendException e) {
            log.error("Failed to send email to {}: {}", dto.getTo(), e.getMessage(), e);
        }
    }

    // =========================================================
    // ACTIVATION EMAIL
    // =========================================================
    public void sendActivationEmail(String to, String activationToken) {
        String activationLink = frontendUrl + "/activate?token=" + activationToken;

        String html = """
                <!DOCTYPE html>
                <html lang="es">
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 40px;">
                  <div style="max-width: 600px; margin: auto; background: #ffffff; border-radius: 8px; padding: 40px;">
                    <h2 style="color: #333333;">Activa tu cuenta en Dinerop</h2>
                    <p style="color: #555555;">Gracias por registrarte. Haz clic en el siguiente botón para activar tu cuenta:</p>
                    <a href="%s"
                       style="display: inline-block; margin-top: 20px; padding: 12px 24px; background-color: #4F46E5;
                              color: #ffffff; text-decoration: none; border-radius: 6px; font-weight: bold;">
                      Activar cuenta
                    </a>
                    <p style="margin-top: 30px; color: #999999; font-size: 12px;">
                      Este enlace expira en 24 horas. Si no creaste una cuenta, ignora este correo.
                    </p>
                  </div>
                </body>
                </html>
                """.formatted(activationLink);

        sendEmail(SendEmailDto.builder()
                .to(to)
                .subject("Activa tu cuenta en Dinerop")
                .htmlContent(html)
                .build());
    }

    // =========================================================
    // RESET PASSWORD EMAIL
    // =========================================================
    public void sendResetPasswordEmail(String to, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        String html = """
                <!DOCTYPE html>
                <html lang="es">
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 40px;">
                  <div style="max-width: 600px; margin: auto; background: #ffffff; border-radius: 8px; padding: 40px;">
                    <h2 style="color: #333333;">Restablecer contraseña</h2>
                    <p style="color: #555555;">Recibimos una solicitud para restablecer tu contraseña. Haz clic en el botón para continuar:</p>
                    <a href="%s"
                       style="display: inline-block; margin-top: 20px; padding: 12px 24px; background-color: #DC2626;
                              color: #ffffff; text-decoration: none; border-radius: 6px; font-weight: bold;">
                      Restablecer contraseña
                    </a>
                    <p style="margin-top: 30px; color: #999999; font-size: 12px;">
                      Este enlace expira en 15 minutos. Si no solicitaste un cambio de contraseña, ignora este correo.
                    </p>
                  </div>
                </body>
                </html>
                """.formatted(resetLink);

        sendEmail(SendEmailDto.builder()
                .to(to)
                .subject("Restablece tu contraseña en Dinerop")
                .htmlContent(html)
                .build());
    }
}
