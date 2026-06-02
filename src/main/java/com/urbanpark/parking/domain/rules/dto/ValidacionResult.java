package com.urbanpark.parking.domain.rules.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidacionResult {

    private final boolean autorizado;
    private final String motivo;

    public static ValidacionResult autorizado() {
        return new ValidacionResult(true, null);
    }

    public static ValidacionResult denegado(String motivo) {
        return new ValidacionResult(false, motivo);
    }
}