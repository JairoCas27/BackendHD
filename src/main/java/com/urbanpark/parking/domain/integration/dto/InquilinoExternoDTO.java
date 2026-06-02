package com.urbanpark.parking.domain.integration.dto;

import lombok.Data;

@Data
public class InquilinoExternoDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private Long apartamentoId;
}