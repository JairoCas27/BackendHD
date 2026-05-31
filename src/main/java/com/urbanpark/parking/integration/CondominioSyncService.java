package com.urbanpark.parking.integration.condominio;

import com.urbanpark.parking.domain.Usuario;
import com.urbanpark.parking.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CondominioSyncService {

    private final CondominioApiClient apiClient;
    private final UsuarioRepository usuarioRepository;

    @Scheduled(fixedRate = 1800000) // Cada 30 minutos
    @Transactional
    public void sincronizarUsuariosProgramado() {
        log.info("Iniciando sincronizacion programada de usuarios: {}", LocalDateTime.now());
        try {
            // Asumiendo un condominio por defecto o configurado
            List<UsuarioExterno> usuarios = apiClient.obtenerUsuariosActivos("1");
            procesarUsuarios(usuarios);
            log.info("Sincronizacion completada. Usuarios procesados: {}", usuarios.size());
        } catch (Exception e) {
            log.error("Error en sincronizacion programada: {}", e.getMessage());
        }
    }

    @Transactional
    public void sincronizarUsuariosPorCondominio(String condominioId) {
        log.info("Sincronizando usuarios del condominio: {}", condominioId);
        List<UsuarioExterno> usuarios = apiClient.obtenerUsuariosActivos(condominioId);
        procesarUsuarios(usuarios);
    }

    private void procesarUsuarios(List<UsuarioExterno> usuariosExternos) {
        for (UsuarioExterno externo : usuariosExternos) {
            Optional<Usuario> existente = usuarioRepository.findByEmail(externo.getEmail());

            if (existente.isPresent()) {
                actualizarUsuarioExistente(existente.get(), externo);
            } else {
                crearNuevoUsuario(externo);
            }
        }
    }

    private void actualizarUsuarioExistente(Usuario local, UsuarioExterno externo) {
        log.debug("Actualizando usuario: {}", externo.getEmail());
        local.setNombre(externo.getNombre());
        local.setApellido(externo.getApellido());
        local.setTelefono(externo.getTelefono());
        local.setActivo(externo.getActivo());
        local.setUltimaSincronizacion(LocalDateTime.now());
        usuarioRepository.save(local);
    }

    private void crearNuevoUsuario(UsuarioExterno externo) {
        log.info("Creando nuevo usuario sincronizado: {}", externo.getEmail());
        Usuario nuevo = Usuario.builder()
                .email(externo.getEmail())
                .nombre(externo.getNombre())
                .apellido(externo.getApellido())
                .telefono(externo.getTelefono())
                .tipoUsuario(externo.getTipoUsuario())
                .condominioId(externo.getCondominioId())
                .activo(externo.getActivo())
                .origen("SINCRONIZADO")
                .ultimaSincronizacion(LocalDateTime.now())
                .build();
        usuarioRepository.save(nuevo);
    }

    @Transactional
    public void sincronizarEspaciosParqueo(String condominioId) {
        log.info("Sincronizando espacios de parqueo del condominio: {}", condominioId);
        List<EspacioParqueo> espacios = apiClient.obtenerEspaciosDisponibles(condominioId);
        // Implementar logica de guardado en repositorio local
        log.info("Espacios sincronizados: {}", espacios.size());
    }

    public boolean verificarConectividad(String condominioId) {
        try {
            apiClient.obtenerEstadoSistema(condominioId);
            return true;
        } catch (Exception e) {
            log.warn("Sin conectividad con condominio {}: {}", condominioId, e.getMessage());
            return false;
        }
    }
}