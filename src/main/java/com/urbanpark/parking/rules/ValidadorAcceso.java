package main.java.com.urbanpark.parking.rules;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class ValidadorAcceso {

    public boolean validarEntrada(Usuario usuario, Vehiculo vehiculo, RestriccionesCondominio restricciones) {
        // Verificar horario
        LocalTime ahora = LocalTime.now();
        if (!restricciones.estaDentroHorarioPermitido(ahora)) {
            return false;
        }

        // Verificar límite de vehículos
        int vehiculosActuales = usuario.getVehiculosActivos().size();
        if (!restricciones.puedeRegistrarVehiculo(vehiculosActuales)) {
            return false;
        }

        // Verificar si el vehículo está activo
        if (!vehiculo.getEstaActivo()) {
            return false;
        }

        return true;
    }

    public boolean validarSalida(Vehiculo vehiculo, RegistroParqueo registro) {
        // Verificar que tenga un registro de entrada activo
        return registro != null && registro.getHoraSalida() == null;
    }

    public boolean esResidente(Usuario usuario) {
        return "RESIDENTE".equals(usuario.getTipoUsuario());
    }

    public boolean esVisitante(Usuario usuario) {
        return "VISITANTE".equals(usuario.getTipoUsuario());
    }
}