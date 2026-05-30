package com.urbanpark.parking.modules.parking;

import java.util.List;

import com.urbanpark.parking.modules.parking.dto.EstacionamientoCreateDto;

public interface EstacionamientoService {
    EstacionamientoEntity crearEstacionamiento(EstacionamientoCreateDto dto);
    List<EstacionamientoEntity> listarPorCondominio();
    EstacionamientoEntity asignarApartamento(String estacionamientoId, String apartamentoId);
}