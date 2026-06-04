package com.urbanpark.parking.domain.notifications.contactanos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanpark.parking.domain.notifications.contactanos.dto.ContactoPublicResponse;
import com.urbanpark.parking.domain.notifications.contactanos.dto.ContactoRequest;
import com.urbanpark.parking.domain.notifications.contactanos.dto.ContactoResponse;
import com.urbanpark.parking.domain.notifications.contactanos.dto.RespuestaRequest;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasRepository;
import com.urbanpark.parking.shared.exceptions.ResourceNotFoundException;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactoMensajeService {

    private final ContactoMensajeRepository repository;
    private final UsuarioSaasRepository usuarioRepository;
    private final JavaMailSender mailSender;

    @Value("${app.contact.destination-email}")
    private String correoDestinoCorporativo;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${app.mail.nombre-remitente:UrbanPark SaaS}")
    private String nombreRemitente;

    @Transactional
    public ContactoPublicResponse registrarMensaje(ContactoRequest request) {
        ContactoMensaje mensaje = ContactoMensaje.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .mensaje(request.getMensaje())
                .build();

        ContactoMensaje guardado = repository.save(mensaje);

        enviarEmail(
            correoDestinoCorporativo,
            "Nuevo Formulario de Contacto - Codigo: " + guardado.getCodigoSeguimiento(),
            "Nombre del remitente: " + guardado.getNombre() + "\n" +
            "Correo del remitente: " + guardado.getCorreo() + "\n\n" +
            "Mensaje:\n" + guardado.getMensaje()
        );

        return mapearAResponsePublico(guardado);
    }

    @Transactional(readOnly = true)
    public List<ContactoResponse> listarTodos() {
        return repository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

 @Transactional(readOnly = true)
    public ContactoPublicResponse buscarPorCodigoSeguimiento(String codigoSeguimiento) {
        ContactoMensaje mensaje = repository.findByCodigoSeguimiento(codigoSeguimiento)
                .orElseThrow(() -> new ResourceNotFoundException("El codigo de seguimiento ingresado no existe en el sistema."));
        return mapearAResponsePublico(mensaje);
    }

    @Transactional
    public ContactoResponse responderMensaje(Long id, RespuestaRequest request, String adminEmail) {
        ContactoMensaje mensaje = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje de contacto no encontrado con ID: " + id));

        var admin = usuarioRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario administrador con el email: " + adminEmail));

        mensaje.setRespuesta(request.getRespuesta());
        mensaje.setFechaRespuesta(LocalDateTime.now());
        mensaje.setRespondido(true);
        mensaje.setUsuarioRespuestaId(admin.getId());

        ContactoMensaje actualizado = repository.save(mensaje);

        // Formateo de fecha legible en formato 24H
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = actualizado.getFechaEnvio().format(formatter);

        enviarEmail(
            actualizado.getCorreo(),
            "UrbanPark - Respuesta a su consulta [" + actualizado.getCodigoSeguimiento() + "]",
            "Estimado(a) " + actualizado.getNombre() + ",\n\n" +
            "Hemos revisado su mensaje enviado el " + fechaFormateada + ".\n\n" +
            "Respuesta de nuestro equipo:\n" + actualizado.getRespuesta() + "\n\n" +
            "Atentamente,\n" + nombreRemitente + "."
        );

        return mapearAResponse(actualizado);
    }

    private void enviarEmail(String para, String asunto, String contenido) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(String.format("%s <%s>", nombreRemitente, mailUsername));
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(contenido);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error al enviar el correo electronico mediante JavaMailSender: " + e.getMessage());
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

    private ContactoPublicResponse mapearAResponsePublico(ContactoMensaje entidad) {
        return ContactoPublicResponse.builder()
                .codigoSeguimiento(entidad.getCodigoSeguimiento())
                .correo(entidad.getCorreo())
                .fechaEnvio(entidad.getFechaEnvio())
                .mensaje(entidad.getMensaje())
                .nombre(entidad.getNombre())
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