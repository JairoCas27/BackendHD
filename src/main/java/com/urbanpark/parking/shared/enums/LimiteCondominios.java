package com.urbanpark.parking.shared.enums;

public enum LimiteCondominios {
    UNO(1),
    TRES(3),
    ILIMITADO(-1);

    private final int valor;

    LimiteCondominios(int valor) { this.valor = valor; }

    public int getValor() { return valor; }

    public boolean permite(long cantidadActual) {
        if (this == ILIMITADO) return true;
        return cantidadActual < this.valor;
    }
}
