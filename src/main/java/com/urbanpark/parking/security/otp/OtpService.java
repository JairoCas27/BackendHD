package com.urbanpark.parking.security.otp;

import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.shared.exceptions.BusinessException;
import com.urbanpark.parking.shared.exceptions.ValidacionException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${otp.expiration-minutes}")
    private int expirationMinutes;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.mail.nombre-remitente}")
    private String nombreRemitente;

    public void generarYEnviar(UsuarioSaas usuario) {
        String codigoPlano = String.valueOf(100000 + new Random().nextInt(900000));
        String codigoHash = passwordEncoder.encode(codigoPlano);

        OtpToken token = OtpToken.builder()
                .usuario(usuario)
                .codigoHash(codigoHash)
                .expiraEn(LocalDateTime.now().plusMinutes(expirationMinutes))
                .usado(false)
                .build();

        otpTokenRepository.save(token);
        enviarEmail(usuario.getEmail(), codigoPlano);
    }

    public void validar(UsuarioSaas usuario, String codigoIngresado) {
        OtpToken token = otpTokenRepository
                .findTopByUsuarioAndUsadoFalseOrderByFechaCreacionDesc(usuario)
                .orElseThrow(() -> new ValidacionException("No existe un OTP activo para este usuario"));

        if (token.getExpiraEn().isBefore(LocalDateTime.now()))
            throw new ValidacionException("El código OTP ha expirado");

        if (!passwordEncoder.matches(codigoIngresado, token.getCodigoHash()))
            throw new ValidacionException("El código OTP es incorrecto");

        token.setUsado(true);
        otpTokenRepository.save(token);
    }

    private void enviarEmail(String destino, String codigo) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(fromEmail, nombreRemitente);
            helper.setTo(destino);
            helper.setSubject("Código de verificación - UrbanPark");
            helper.setText(
                    "Tu código de verificación es: " + codigo +
                            "\n\nEste código expira en " + expirationMinutes + " minutos." +
                            "\n\nSi no solicitaste esto, ignora este mensaje." +
                            "\n\n— Equipo UrbanPark",
                    false
            );

            mailSender.send(mensaje);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new BusinessException("Error al enviar el correo: " + e.getMessage());
        }
    }
}