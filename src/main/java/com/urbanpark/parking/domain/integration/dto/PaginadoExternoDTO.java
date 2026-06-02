package com.urbanpark.parking.domain.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PaginadoExternoDTO<T> {

    @JsonProperty("contenido")
    private List<T> contenido;

    @JsonProperty("pagina")
    private int pagina;

    @JsonProperty("tamanio")
    private int tamanio;

    @JsonProperty("totalElementos")
    private long totalElementos;

    @JsonProperty("totalPaginas")
    private int totalPaginas;
}