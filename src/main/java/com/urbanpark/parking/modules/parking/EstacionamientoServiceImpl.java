package com.urbanpark.parking.modules.parking;

import com.urbanpark.parking.core.exception.BusinessException;
import com.urbanpark.parking.modules.parking.client.CondominioClient;
import com.urbanpark.parking.modules.parking.dto.ApartamentoExternoDto;
import com.urbanpark.parking.modules.parking.dto.EstacionamientoCreateDto;
import com.urbanpark.parking.tenant.TenantContext;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstacionamientoServiceImpl implements EstacionamientoService {

    private final EstacionamientoRepository repository;
    private final CondominioClient condominioClient; // Inyectamos el cliente externo

    public EstacionamientoServiceImpl(EstacionamientoRepository repository, CondominioClient condominioClient) {
        this.repository = repository;
        this.condominioClient = condominioClient;
    }

    @Override
    public EstacionamientoEntity crearEstacionamiento(EstacionamientoCreateDto dto) {
        String tenantId = TenantContext.getCurrentTenant(); 
        
        EstacionamientoEntity estacionamiento = new EstacionamientoEntity();
        estacionamiento.setCondominioId(tenantId);
        estacionamiento.setCodigo(dto.getCodigo());
        estacionamiento.setSector(dto.getSector());
        estacionamiento.setEstado(EstacionamientoEntity.EstadoParking.DISPONIBLE);

        return repository.save(estacionamiento);
    }

    @Override
    public List<EstacionamientoEntity> listarPorCondominio() {
        String tenantId = TenantContext.getCurrentTenant();
        return repository.findByCondominioId(tenantId);
    }

    @Override
    public EstacionamientoEntity asignarApartamento(String estacionamientoId, String apartamentoId) {
        String tenantId = TenantContext.getCurrentTenant();
        
        // 1. Verificar existencia del estacionamiento local
        EstacionamientoEntity espacio = repository.findByIdAndCondominioId(estacionamientoId, tenantId)
                .orElseThrow(() -> new BusinessException("El espacio de estacionamiento no existe en este condominio."));
        
        // 2. Consumir API del otro grupo para validar existencia y leer el límite de vehículos
        ApartamentoExternoDto apartamentoExt = condominioClient.obtenerApartamento(apartamentoId);
        if (apartamentoExt == null) {
            throw new BusinessException("El apartamento especificado no existe en el sistema del condominio.");
        }

        // 3. Contar cuántos estacionamientos ya tiene ocupados ese apartamento en nuestro SaaS
        long cocherasAsignadas = repository.countByApartamentoIdAndCondominioId(apartamentoId, tenantId);

        // 4. ALGORITMO: Evaluar el motor de reglas (maxVehiculosPermitidos)
        if (cocherasAsignadas >= apartamentoExt.getMaxVehiculosPermitidos()) {
            throw new BusinessException("Asignación rechazada: El apartamento " + apartamentoExt.getNumero() + 
                    " ya alcanzó el límite máximo permitido de " + apartamentoExt.getMaxVehiculosPermitidos() + " vehículos.");
        }
        
        // 5. Si tiene cupo libre, se procede a la asignación física
        espacio.setApartamentoId(apartamentoId); 
        espacio.setEstado(EstacionamientoEntity.EstadoParking.OCUPADO);
        return repository.save(espacio);
    }
}