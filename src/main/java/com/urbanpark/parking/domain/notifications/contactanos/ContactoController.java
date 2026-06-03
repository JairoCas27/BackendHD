package com.urbanpark.parking.domain.notifications.contactanos;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urbanpark.parking.domain.notifications.contactanos.dto.ContactoRequest;
import com.urbanpark.parking.domain.notifications.contactanos.dto.ContactoResponse;
import com.urbanpark.parking.domain.notifications.contactanos.dto.RespuestaRequest;
import com.urbanpark.parking.shared.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/contacto")
@RequiredArgsConstructor
@Tag(name = "Contacto", description = "Endpoints públicos y administrativos para el formulario de contacto de la Landing Page")
public class ContactoController {

    private final ContactoMensajeService service;

    @PostMapping
    @Operation(summary = "Registrar un nuevo mensaje de contacto (Público)", 
               description = "Cualquier usuario anónimo puede rellenar nombre, correo y mensaje desde la Landing Page. Genera un código único de seguimiento y envía una alerta por correo.")
    public ResponseEntity<ApiResponse<ContactoResponse>> enviarMensaje(
            @RequestBody @Valid ContactoRequest request) {
        ContactoResponse response = service.registrarMensaje(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Mensaje enviado y registrado con éxito", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'SUPERADMIN')")
    @Operation(summary = "Listar todos los mensajes de contacto recibidos", 
               description = "Permite a los usuarios con rol ADMIN o SUPERADMIN listar y auditar todas las consultas enviadas por los usuarios.")
    public ResponseEntity<ApiResponse<List<ContactoResponse>>> listarMensajes() {
        List<ContactoResponse> mensajes = service.listarTodos();
        return ResponseEntity.ok(ApiResponse.success("Listado de mensajes recuperado con éxito", mensajes));
    }

    // ✨ MEJORA 1: Nuevo endpoint público para rastreo de estados desde la Landing Page sin Token
    @GetMapping("/seguimiento/{codigo}")
    @Operation(summary = "Obtener un mensaje de contacto por su código de seguimiento (Público)", 
               description = "Permite a los clientes finales buscar una consulta directamente ingresando su código único (Ej: CON-A318BC16).")
    public ResponseEntity<ApiResponse<ContactoResponse>> buscarPorCodigo(@PathVariable String codigo) {
        ContactoResponse response = service.buscarPorCodigoSeguimiento(codigo);
        return ResponseEntity.ok(ApiResponse.success("Consulta recuperada con éxito", response));
    }

    // ✨ MEJORA 2: Endpoint de respuesta enriquecido con auditoría automática mediante Spring Security
    @PatchMapping("/{id}/responder")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'SUPERADMIN')")
    @Operation(summary = "Responder a un mensaje de contacto específico", 
               description = "Registra la respuesta en el sistema inyectando automáticamente la cuenta del Administrador logueado y enviando la resolución por email.")
    public ResponseEntity<ApiResponse<ContactoResponse>> responderMensaje(
            @PathVariable Long id,
            @RequestBody @Valid RespuestaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Extraemos el email/username del administrador autenticado que ejecuta la operación
        String adminEmail = userDetails.getUsername();
        
        ContactoResponse response = service.responderMensaje(id, request, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Respuesta enviada correctamente al remitente", response));
    }
}