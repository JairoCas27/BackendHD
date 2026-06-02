package com.urbanpark.parking.domain.integration;

import com.urbanpark.parking.domain.integration.dto.SyncResultDTO;
import com.urbanpark.parking.domain.integration.dto.UsuarioExternoSyncDTO;
import com.urbanpark.parking.domain.integration.dto.VehiculoExternoSyncDTO;
import com.urbanpark.parking.domain.tenant.Condominio;
import com.urbanpark.parking.domain.tenant.CondominioRepository;
import com.urbanpark.parking.domain.users.usuario.UsuarioCondominio;
import com.urbanpark.parking.domain.users.usuario.UsuarioCondominioRepository;
import com.urbanpark.parking.domain.users.vehiculo.Vehiculo;
import com.urbanpark.parking.domain.users.vehiculo.VehiculoRepository;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import com.urbanpark.parking.shared.enums.EstadoSync;
import com.urbanpark.parking.shared.enums.RolParking;
import com.urbanpark.parking.shared.enums.TipoVehiculo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

    private final CondominioRepository condominioRepository;
    private final IntegrationClient integrationClient;
    private final SyncLogRepository syncLogRepository;
    private final UsuarioCondominioRepository usuarioRepository;
    private final VehiculoRepository vehiculoRepository;

    @Scheduled(fixedDelay = 6 * 60 * 60 * 1000)
    public void sincronizarTodos() {
        log.info("Iniciando sincronización global...");

        List<Condominio> activos = condominioRepository.findAllByEstado(EstadoCondominio.ACTIVO);

        for (Condominio condominio : activos) {
            try {
                if (!integrationClient.verificarConexion(condominio)) {
                    log.warn("[{}] API no disponible, saltando sync", condominio.getNombre());
                    continue;
                }
                sincronizarCondominio(condominio);
            } catch (Exception e) {
                log.error("Error sincronizando [{}]: {}", condominio.getNombre(), e.getMessage());
            }
        }

        log.info("Sincronización global completada.");
    }

    public SyncResultDTO sincronizarCondominio(Condominio condominio) {
        log.info("Sincronizando: {}", condominio.getNombre());

        SyncResultDTO usuariosResult  = sincronizarUsuarios(condominio);
        SyncResultDTO vehiculosResult = sincronizarVehiculos(condominio);

        return SyncResultDTO.builder()
                .tenantId(condominio.getId())
                .usuariosCreados(usuariosResult.getUsuariosCreados())
                .usuariosActualizados(usuariosResult.getUsuariosActualizados())
                .vehiculosCreados(vehiculosResult.getVehiculosCreados())
                .vehiculosActualizados(vehiculosResult.getVehiculosActualizados())
                .build();
    }

    private SyncResultDTO sincronizarUsuarios(Condominio condominio) {
        long inicio = System.currentTimeMillis();
        int creados = 0, actualizados = 0;

        try {
            List<UsuarioExternoSyncDTO> externos = integrationClient.obtenerUsuarios(condominio);

            for (UsuarioExternoSyncDTO dto : externos) {
                if (!dto.isActivo()) continue;

                var existente = usuarioRepository.findByExternalIdAndTenantId(
                        String.valueOf(dto.getId()), condominio.getId());

                if (existente.isEmpty()) {
                    usuarioRepository.save(UsuarioCondominio.builder()
                            .externalId(String.valueOf(dto.getId()))
                            .tenantId(condominio.getId())
                            .nombre(dto.getNombres() + " " + dto.getApellidos())
                            .email(dto.getCorreo())
                            .rolParking(mapearRol(dto.getRol()))
                            .activo(true)
                            .syncedAt(LocalDateTime.now())
                            .build());
                    creados++;
                } else {
                    UsuarioCondominio usuario = existente.get();
                    usuario.setNombre(dto.getNombres() + " " + dto.getApellidos());
                    usuario.setEmail(dto.getCorreo());
                    usuario.setRolParking(mapearRol(dto.getRol()));
                    usuario.setSyncedAt(LocalDateTime.now());
                    usuarioRepository.save(usuario);
                    actualizados++;
                }
            }

            guardarSyncLog(condominio, "USUARIOS", EstadoSync.EXITOSO,
                    externos.size(), creados, actualizados, null,
                    System.currentTimeMillis() - inicio);

            log.info("[{}] Usuarios — creados: {}, actualizados: {}",
                    condominio.getNombre(), creados, actualizados);

        } catch (Exception e) {
            guardarSyncLog(condominio, "USUARIOS", EstadoSync.FALLIDO,
                    0, 0, 0, e.getMessage(), System.currentTimeMillis() - inicio);
            log.error("[{}] Error sync usuarios: {}", condominio.getNombre(), e.getMessage());
        }

        return SyncResultDTO.builder()
                .usuariosCreados(creados)
                .usuariosActualizados(actualizados)
                .build();
    }

    private SyncResultDTO sincronizarVehiculos(Condominio condominio) {
        long inicio = System.currentTimeMillis();
        int creados = 0, actualizados = 0;

        try {
            List<VehiculoExternoSyncDTO> externos = integrationClient.obtenerVehiculos(condominio);

            for (VehiculoExternoSyncDTO dto : externos) {
                if (dto.getPlaca() == null || dto.getPlaca().isBlank()) continue;

                Long externalOwnerId = dto.getPropietarioId() != null
                        ? dto.getPropietarioId()
                        : dto.getInquilinoId();

                if (externalOwnerId == null) continue;

                var usuario = usuarioRepository.findByExternalIdAndTenantId(
                        String.valueOf(externalOwnerId), condominio.getId());

                if (usuario.isEmpty()) continue;

                var existente = vehiculoRepository.findByPlacaAndTenantId(
                        dto.getPlaca().toUpperCase(), condominio.getId());

                if (existente.isEmpty()) {
                    vehiculoRepository.save(Vehiculo.builder()
                            .tenantId(condominio.getId())
                            .usuarioId(usuario.get().getId())
                            .placa(dto.getPlaca().toUpperCase())
                            .marca(dto.getMarca())
                            .modelo(dto.getModelo())
                            .color(dto.getColor())
                            .tipo(mapearTipoVehiculo(dto.getTipo()))
                            .activo(true)
                            .build());
                    creados++;
                } else {
                    Vehiculo vehiculo = existente.get();
                    vehiculo.setMarca(dto.getMarca());
                    vehiculo.setModelo(dto.getModelo());
                    vehiculo.setColor(dto.getColor());
                    vehiculo.setTipo(mapearTipoVehiculo(dto.getTipo()));
                    vehiculoRepository.save(vehiculo);
                    actualizados++;
                }
            }

            guardarSyncLog(condominio, "VEHICULOS", EstadoSync.EXITOSO,
                    externos.size(), creados, actualizados, null,
                    System.currentTimeMillis() - inicio);

            log.info("[{}] Vehículos — creados: {}, actualizados: {}",
                    condominio.getNombre(), creados, actualizados);

        } catch (Exception e) {
            guardarSyncLog(condominio, "VEHICULOS", EstadoSync.FALLIDO,
                    0, 0, 0, e.getMessage(), System.currentTimeMillis() - inicio);
            log.error("[{}] Error sync vehículos: {}", condominio.getNombre(), e.getMessage());
        }

        return SyncResultDTO.builder()
                .vehiculosCreados(creados)
                .vehiculosActualizados(actualizados)
                .build();
    }

    private void guardarSyncLog(Condominio condominio, String tipo, EstadoSync estado,
                                int procesados, int creados, int actualizados, String error, long duracionMs) {
        syncLogRepository.save(SyncLog.builder()
                .tenantId(condominio.getId())
                .tipo(tipo)
                .estado(estado)
                .registrosProcesados(procesados)
                .registrosCreados(creados)
                .registrosActualizados(actualizados)
                .mensajeError(error)
                .duracionMs(duracionMs)
                .build());
    }

    private TipoVehiculo mapearTipoVehiculo(String tipo) {
        if (tipo == null) return TipoVehiculo.RESIDENTE;
        return switch (tipo.toUpperCase()) {
            case "MOTO"      -> TipoVehiculo.MOTO;
            case "VISITANTE" -> TipoVehiculo.VISITANTE;
            default          -> TipoVehiculo.RESIDENTE;
        };
    }

    private RolParking mapearRol(String rolExterno) {
        if (rolExterno == null) return RolParking.PROPIETARIO;
        return switch (rolExterno.toUpperCase()) {
            case "ADMINISTRADOR_CONDOMINIO"          -> RolParking.ADMIN_CONDOMINIO;
            case "AGENTE_SEGURIDAD", "SEGURIDAD",
                 "VIGILANTE"                         -> RolParking.AGENTE_SEGURIDAD;
            default                                  -> RolParking.PROPIETARIO;
        };
    }
}