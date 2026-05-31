package com.urbanpark.parking.modules.security;

import com.urbanpark.parking.modules.security.dto.ControlAccesoDto;
import com.urbanpark.parking.modules.security.dto.RespuestaAccesoDto;

public interface ReglasAccesoService {
    RespuestaAccesoDto evaluarAcceso(ControlAccesoDto solicitud);
}