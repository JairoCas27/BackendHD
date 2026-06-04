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

import com.urbanpark.parking.domain.notifications.contactanos.dto.ContactoPublicResponse;
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

  // Cambia únicamente el método enviarMensaje dentro de tu ContactoController por este:
    @PostMapping
    @Operation(summary = "Registrar un nuevo mensaje de contacto (Público)", 
               description = "Cualquier usuario anónimo puede rellenar nombre, correo y mensaje desde la Landing Page. Genera un código único de seguimiento y envía una alerta por correo.")
    public ResponseEntity<ApiResponse<ContactoPublicResponse>> enviarMensaje(
            @RequestBody @Valid ContactoRequest request) {
        ContactoPublicResponse response = service.registrarMensaje(request);
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

    @GetMapping("/seguimiento/{codigo}")
    @Operation(summary = "Obtener un mensaje de contacto por su código de seguimiento (Público)", 
               description = "Permite a los clientes finales buscar una consulta directamente ingresando su código único (Ej: CON-A318BC16).")
    public ResponseEntity<ApiResponse<ContactoPublicResponse>> buscarPorCodigo(@PathVariable String codigo) {
        ContactoPublicResponse response = service.buscarPorCodigoSeguimiento(codigo);
        return ResponseEntity.ok(ApiResponse.success("Consulta recuperada con éxito", response));
    }

    @PatchMapping("/{id}/responder")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'SUPERADMIN')")
    @Operation(summary = "Responder a un mensaje de contacto específico", 
               description = "Registra la respuesta en el sistema inyectando automáticamente el ID del Administrador logueado y enviando la resolución por email.")
    public ResponseEntity<ApiResponse<ContactoResponse>> responderMensaje(
            @PathVariable Long id,
            @RequestBody @Valid RespuestaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Extraemos el identificador (email) del contexto de Spring Security
        String adminEmail = userDetails.getUsername();
        
        ContactoResponse response = service.responderMensaje(id, request, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Respuesta enviada correctamente al remitente", response));
    }

    @GetMapping("/respondidos")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'SUPERADMIN')")
    @Operation(summary = "Listar mensajes de contacto ya respondidos (Privado)", 
               description = "Permite a los administradores auditar el historial de consultas resueltas.")
    public ResponseEntity<ApiResponse<List<ContactoResponse>>> obtenerRespondidos() {
        List<ContactoResponse> mensajes = service.listarRespondidos();
        
        String mensajeInformativo = mensajes.isEmpty() 
                ? "No se han registrado mensajes respondidos en el sistema todavía." 
                : "Listado de mensajes respondidos recuperado con éxito";
                
        return ResponseEntity.ok(ApiResponse.success(mensajeInformativo, mensajes));
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN_CONDOMINIO', 'SUPERADMIN')")
    @Operation(summary = "Listar mensajes de contacto pendientes de respuesta (Privado)", 
               description = "Muestra únicamente las consultas que requieren atención inmediata. Si está vacío, confirma que todo está resuelto.")
    public ResponseEntity<ApiResponse<List<ContactoResponse>>> obtenerPendientes() {
        List<ContactoResponse> mensajes = service.listarPendientes();
        
        String mensajeInformativo = mensajes.isEmpty() 
                ? "Todos los mensajes han sido respondidos con éxito." 
                : "Listado de mensajes pendientes recuperado con éxito";
                
        return ResponseEntity.ok(ApiResponse.success(mensajeInformativo, mensajes));
    }
}