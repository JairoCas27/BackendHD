package com.urbanpark.parking.domain.usuarios;

import com.urbanpark.parking.domain.usuarios.dto.*;
import com.urbanpark.parking.shared.enums.EstadoUsuarioSaas;
import com.urbanpark.parking.shared.enums.OrigenRegistro;
import com.urbanpark.parking.shared.enums.RolSaas;
import com.urbanpark.parking.shared.exceptions.AccesoDenegadoException;
import com.urbanpark.parking.shared.exceptions.ResourceNotFoundException;
import com.urbanpark.parking.shared.exceptions.ValidacionException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioSaasService {

    private final UsuarioSaasRepository usuarioSaasRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── Obtener usuario autenticado actual ──────────────────
    public UsuarioSaas getUsuarioActual() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return usuarioSaasRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    // ─── Crear ADMIN o SUPERADMIN ────────────────────────────
    @Transactional
    public UsuarioSaasResponse crearUsuarioInterno(CrearUsuarioAdminRequest request) {
        UsuarioSaas creador = getUsuarioActual();

        // Solo ADMIN o SUPERADMIN permitidos como rol destino
        if (request.getRol() == RolSaas.CLIENTE)
            throw new ValidacionException("No puedes crear un usuario con rol CLIENTE desde este endpoint");

        // Solo SUPERADMIN puede crear otro SUPERADMIN
        if (request.getRol() == RolSaas.SUPERADMIN
                && creador.getRol() != RolSaas.SUPERADMIN)
            throw new AccesoDenegadoException("Solo un SUPERADMIN puede crear otro SUPERADMIN");

        if (usuarioSaasRepository.existsByEmail(request.getEmail()))
            throw new ValidacionException("Ya existe un usuario con el email: " + request.getEmail());

        if (usuarioSaasRepository.existsByDni(request.getDni()))
            throw new ValidacionException("Ya existe un usuario con el DNI: " + request.getDni());

        OrigenRegistro origen = creador.getRol() == RolSaas.SUPERADMIN
                ? OrigenRegistro.SUPERADMIN
                : OrigenRegistro.ADMIN;

        UsuarioSaas nuevo = UsuarioSaas.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .dni(request.getDni())
                .telefono(request.getTelefono())
                .rol(request.getRol())
                .estado(EstadoUsuarioSaas.ACTIVO)
                .origenRegistro(origen)
                .creadoPor(creador)
                .esBaseProtegido(false)
                .build();

        usuarioSaasRepository.save(nuevo);
        return toResponse(nuevo);
    }

    // ─── Listar todos los usuarios internos (ADMIN + SUPERADMIN) ─
    public List<UsuarioSaasResponse> listarUsuariosInternos() {
        return usuarioSaasRepository.findAllByRolIn(
                        List.of(RolSaas.ADMIN, RolSaas.SUPERADMIN))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ─── Listar clientes ──────────────────────────────────────
    public List<UsuarioSaasResponse> listarClientes() {
        return usuarioSaasRepository.findAllByRol(RolSaas.CLIENTE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ─── Obtener por ID ───────────────────────────────────────
    public UsuarioSaasResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    // ─── Actualizar estado ────────────────────────────────────
    @Transactional
    public UsuarioSaasResponse actualizarEstado(Long id, ActualizarEstadoRequest request) {
        UsuarioSaas usuario = findById(id);

        if (usuario.isEsBaseProtegido())
            throw new AccesoDenegadoException("El SUPERADMIN base no puede ser modificado");

        usuario.setEstado(request.getEstado());
        usuarioSaasRepository.save(usuario);
        return toResponse(usuario);
    }

    // ─── Eliminar ─────────────────────────────────────────────
    @Transactional
    public void eliminar(Long id) {
        UsuarioSaas usuario = findById(id);

        if (usuario.isEsBaseProtegido())
            throw new AccesoDenegadoException("El SUPERADMIN base no puede ser eliminado");

        usuarioSaasRepository.delete(usuario);
    }

    // ─── Helpers ──────────────────────────────────────────────
    private UsuarioSaas findById(Long id) {
        return usuarioSaasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    public UsuarioSaasResponse toResponse(UsuarioSaas u) {
        return UsuarioSaasResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .nombres(u.getNombres())
                .apellidos(u.getApellidos())
                .nombreCompleto(u.getNombreCompleto())
                .dni(u.getDni())
                .telefono(u.getTelefono())
                .rol(u.getRol())
                .estado(u.getEstado())
                .origenRegistro(u.getOrigenRegistro())
                .creadoPorId(u.getCreadoPor() != null ? u.getCreadoPor().getId() : null)
                .creadoPorNombre(u.getCreadoPor() != null ? u.getCreadoPor().getNombreCompleto() : null)
                .esBaseProtegido(u.isEsBaseProtegido())
                .fechaRegistro(u.getFechaRegistro())
                .build();
    }

    public UsuarioSaas guardar(UsuarioSaas usuario) {
        return usuarioSaasRepository.save(usuario);
    }
}