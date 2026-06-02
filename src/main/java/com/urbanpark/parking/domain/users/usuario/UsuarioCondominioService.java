package com.urbanpark.parking.domain.users.usuario;

import com.urbanpark.parking.domain.users.usuario.dto.UsuarioResponse;
import com.urbanpark.parking.domain.tenant.TenantContext;
import com.urbanpark.parking.shared.enums.RolParking;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioCondominioService {

    private final UsuarioCondominioRepository usuarioRepository;

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository
                .findAllByTenantId(TenantContext.getTenantId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UsuarioResponse> listarPorRol(RolParking rol) {
        return usuarioRepository
                .findAllByTenantIdAndRolParking(TenantContext.getTenantId(), rol)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(UUID id) {
        return toResponse(findById(id));
    }

    public void activar(UUID id) {
        UsuarioCondominio usuario = findById(id);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    public void desactivar(UUID id) {
        UsuarioCondominio usuario = findById(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private UsuarioCondominio findById(UUID id) {
        UsuarioCondominio usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar que pertenece al tenant activo
        if (!usuario.getTenantId().equals(TenantContext.getTenantId())) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }

        return usuario;
    }

    private UsuarioResponse toResponse(UsuarioCondominio u) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .externalId(u.getExternalId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .rolParking(u.getRolParking())
                .activo(u.isActivo())
                .syncedAt(u.getSyncedAt())
                .build();
    }
}