package com.urbanpark.parking.domain;

public enum NotificationTemplate {
    ENTRADA_VEHICULO("Ingreso Vehicular", "El vehículo con placa %s ha ingresado al estacionamiento."),
    SALIDA_VEHICULO("Salida Vehicular", "El vehículo con placa %s ha salido del estacionamiento."),
    ACCESO_DENEGADO("Alerta de Seguridad", "Intento de acceso denegado para la placa %s fuera de horario.");

    private final String title;
    private final String messageTemplate;

    NotificationTemplate(String title, String messageTemplate) {
        this.title = title;
        this.messageTemplate = messageTemplate;
    }

    public String getTitle() { return title; }
    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}

