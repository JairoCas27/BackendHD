package com.urbanpark.parking.modules.security;

import com.urbanpark.parking.modules.parking.EstacionamientoRepository;
import com.urbanpark.parking.modules.parking.EstacionamientoEntity;
import com.urbanpark.parking.modules.security.dto.ControlAccesoDto;
import com.urbanpark.parking.modules.security.dto.RespuestaAccesoDto;
import com.urbanpark.parking.tenant.TenantContext;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReglasAccesoServiceImpl implements ReglasAccesoService {

    private final EstacionamientoRepository parkingRepository;

    public ReglasAccesoServiceImpl(EstacionamientoRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    @Override
    public RespuestaAccesoDto evaluarAcceso(ControlAccesoDto solicitud) {
        String tenantId = TenantContext.getCurrentTenant(); // Aislamiento Multi-Tenant

        // REGLA 1: Simulación académica de estado del Apartamento (Integración lógica)
        // Si el apartamento termina en "99" simulamos que está INACTIVO o con DEUDAS
        if (solicitud.getApartamentoId() != null && solicitud.getApartamentoId().endsWith("99")) {
            return new RespuestaAccesoDto(
                false,
                "ACCESO DENEGADO: El apartamento asociado presenta restricciones administrativas (Inactivo/Deuda).",
                "MANTENER_CERRADO"
            );
        }

        // REGLA 2: Validar el estado físico de los estacionamientos asignados a ese apartamento en nuestro SaaS
        List<EstacionamientoEntity> cocheras = parkingRepository.findByCondominioId(tenantId);
        
        boolean tieneCeldaHabilitada = cocheras.stream()
            .filter(c -> solicitud.getApartamentoId().equals(c.getApartamentoId()))
            .anyMatch(c -> c.getEstado() == EstacionamientoEntity.EstadoParking.DISPONIBLE || 
                           c.getEstado() == EstacionamientoEntity.EstadoParking.OCUPADO);

        boolean tieneCeldaEnMantenimiento = cocheras.stream()
            .filter(c -> solicitud.getApartamentoId().equals(c.getApartamentoId()))
            .anyMatch(c -> c.getEstado() == EstacionamientoEntity.EstadoParking.MANTENIMIENTO);

        if (tieneCeldaEnMantenimiento && !tieneCeldaHabilitada) {
            return new RespuestaAccesoDto(
                false,
                "ACCESO DENEGADO: La plaza de estacionamiento asignada a este apartamento está en MANTENIMIENTO.",
                "MANTENER_CERRADO"
            );
        }

        // REGLA 3: Si todo está en orden, se autoriza el ingreso o salida
        String accion = "INGRESO".equalsIgnoreCase(solicitud.getTipoAcceso()) ? "ABRIR_BARRERA_ENTRADA" : "ABRIR_BARRERA_SALIDA";
        return new RespuestaAccesoDto(
            true,
            "ACCESO AUTORIZADO: Vehículo con placa [" + solicitud.getPlaca() + "] validado correctamente.",
            accion
        );
    }
}