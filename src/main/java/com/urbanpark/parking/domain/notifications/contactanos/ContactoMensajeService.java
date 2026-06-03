package com.urbanpark.parking.domain.notifications.contactanos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanpark.parking.domain.notifications.contactanos.dto.ContactoRequest;
import com.urbanpark.parking.domain.notifications.contactanos.dto.ContactoResponse;
import com.urbanpark.parking.domain.notifications.contactanos.dto.RespuestaRequest;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactoMensajeService {

    private final ContactoMensajeRepository repository;

    @Value("${app.contact.destination-email}")
    private String correoDestinoCorporativo;

    // Inyectamos las propiedades de Spring para el entorno de correo
    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private String mailPort;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Transactional
    public ContactoResponse registrarMensaje(ContactoRequest request) {
        // 1. Guardar el mensaje en la base de datos de Neon
        ContactoMensaje mensaje = ContactoMensaje.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .mensaje(request.getMensaje())
                .build();

        ContactoMensaje guardado = repository.save(mensaje);

        // 2. Enviar el correo de alerta al buzón corporativo de la empresa
        enviarEmail(
            correoDestinoCorporativo,
            "Nuevo Formulario de Contacto - Código: " + guardado.getCodigoSeguimiento(),
            "Nombre del remitente: " + guardado.getNombre() + "\n" +
            "Correo del remitente: " + guardado.getCorreo() + "\n\n" +
            "Mensaje:\n" + guardado.getMensaje()
        );

        return mapearAResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<ContactoResponse> listarTodos() {
        return repository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContactoResponse responderMensaje(Long id, RespuestaRequest request) {
        ContactoMensaje mensaje = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje de contacto no encontrado con ID: " + id));

        mensaje.setRespuesta(request.getRespuesta());
        mensaje.setFechaRespuesta(LocalDateTime.now());
        mensaje.setRespondido(true);

        ContactoMensaje actualizado = repository.save(mensaje);

        // Enviar la respuesta vía correo electrónico al remitente original
        enviarEmail(
            actualizado.getCorreo(),
            "Respuesta a su consulta UrbanPark - Código: " + actualizado.getCodigoSeguimiento(),
            "Estimado(a) " + actualizado.getNombre() + ",\n\n" +
            "Hemos revisado su mensaje enviado el " + actualizado.getFechaEnvio() + ".\n\n" +
            "Respuesta de nuestro equipo:\n" + actualizado.getRespuesta() + "\n\n" +
            "Atentamente,\nSoporte UrbanPark."
        );

        return mapearAResponse(actualizado);
    }

    private void enviarEmail(String para, String asunto, String contenido) {
        // Configuración de propiedades SMTP utilizando la infraestructura de Jakarta
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", mailHost);
        props.put("mail.smtp.port", mailPort);
        props.put("mail.smtp.ssl.trust", mailHost);

        // Crear la sesión con autenticación básica
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailUsername, mailPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailUsername.isEmpty() ? "no-reply@urbanpark.com" : mailUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(para));
            message.setSubject(asunto);
            message.setText(contenido);

            Transport.send(message);
        } catch (MessagingException e) {
            // Se captura la excepción pero no detiene el flujo de la base de datos si falla el SMTP local
            System.err.println("Error al enviar el correo electrónico por SMTP: " + e.getMessage());
        }
    }

    private ContactoResponse mapearAResponse(ContactoMensaje entidad) {
        return ContactoResponse.builder()
                .id(entidad.getId())
                .nombre(entidad.getNombre())
                .correo(entidad.getCorreo())
                .mensaje(entidad.getMensaje())
                .codigoSeguimiento(entidad.getCodigoSeguimiento())
                .fechaEnvio(entidad.getFechaEnvio())
                .respondido(entidad.isRespondido())
                .respuesta(entidad.getRespuesta())
                .fechaRespuesta(entidad.getFechaRespuesta())
                .build();
    }
}