package com.urbanpark.parking.domain.notifications.contactanos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanpark.parking.domain.notifications.contactanos.dto.ContactoRequest;
import com.urbanpark.parking.domain.notifications.contactanos.dto.ContactoResponse;
import com.urbanpark.parking.domain.notifications.contactanos.dto.RespuestaRequest;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasRepository;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactoMensajeService {

    private final ContactoMensajeRepository repository;
    private final UsuarioSaasRepository usuarioRepository;
    
    // ✨ MEJORA: Inyectamos el componente nativo de Spring que pidió tu compañero
    private final JavaMailSender mailSender;

    @Value("${app.contact.destination-email}")
    private String correoDestinoCorporativo;

    // ✨ MEJORA: Jalamos el nombre del remitente e email de autenticación desde el properties
    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${app.mail.nombre-remitente:UrbanPark SaaS}")
    private String nombreRemitente;

    @Transactional
    public ContactoResponse registrarMensaje(ContactoRequest request) {
        ContactoMensaje mensaje = ContactoMensaje.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .mensaje(request.getMensaje())
                .build();

        ContactoMensaje guardado = repository.save(mensaje);

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

    @Transactional(readOnly = true)
    public ContactoResponse buscarPorCodigoSeguimiento(String codigoSeguimiento) {
        ContactoMensaje mensaje = repository.findByCodigoSeguimiento(codigoSeguimiento)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún mensaje de contacto con el código: " + codigoSeguimiento));
        return mapearAResponse(mensaje);
    }

    @Transactional
    public ContactoResponse responderMensaje(Long id, RespuestaRequest request, String adminEmail) {
        ContactoMensaje mensaje = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje de contacto no encontrado con ID: " + id));

        var admin = usuarioRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario administrador con el email: " + adminEmail));

        mensaje.setRespuesta(request.getRespuesta());
        mensaje.setFechaRespuesta(LocalDateTime.now());
        mensaje.setRespondido(true);
        mensaje.setUsuarioRespuestaId(admin.getId());

        ContactoMensaje actualizado = repository.save(mensaje);

        enviarEmail(
            actualizado.getCorreo(),
            "Respuesta a su consulta UrbanPark - Código: " + actualizado.getCodigoSeguimiento(),
            "Estimado(a) " + actualizado.getNombre() + ",\n\n" +
            "Hemos revisado su mensaje enviado el " + actualizado.getFechaEnvio() + ".\n\n" +
            "Respuesta de nuestro equipo:\n" + actualizado.getRespuesta() + "\n\n" +
            "Atentamente,\n" + nombreRemitente + "."
        );

        return mapearAResponse(actualizado);
    }

    // ✨ MEJORA: El método ahora es ultra compacto gracias a JavaMailSender
    private void enviarEmail(String para, String asunto, String contenido) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Configura el remitente combinando el nombre estético con el correo real de salida
            helper.setFrom(String.format("%s <%s>", nombreRemitente, mailUsername));
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(contenido);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error al enviar el correo electrónico mediante JavaMailSender: " + e.getMessage());
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
                .usuarioRespuestaId(entidad.getUsuarioRespuestaId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ContactoResponse> listarRespondidos() {
        return repository.findByRespondidoTrue().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContactoResponse> listarPendientes() {
        return repository.findByRespondidoFalse().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }
}