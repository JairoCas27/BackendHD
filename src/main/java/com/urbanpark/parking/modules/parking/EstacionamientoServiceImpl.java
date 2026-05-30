package com.urbanpark.parking.modules.parking;

import com.urbanpark.parking.core.exception.BusinessException;
import com.urbanpark.parking.modules.parking.dto.EstacionamientoCreateDto;
import com.urbanpark.parking.tenant.TenantContext;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstacionamientoServiceImpl implements EstacionamientoService {

    private final EstacionamientoRepository repository;

    public EstacionamientoServiceImpl(EstacionamientoRepository repository) {
        this.repository = repository;
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
        EstacionamientoEntity espacio = repository.findByIdAndCondominioId(estacionamientoId, tenantId)
                .orElseThrow(() -> new BusinessException("El espacio de estacionamiento no existe en este condominio."));
        
        espacio.setApartamentoId(apartamentoId); 
        return repository.save(espacio);
    }
}