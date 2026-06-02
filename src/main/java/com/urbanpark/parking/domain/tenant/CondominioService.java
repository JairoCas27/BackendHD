package com.urbanpark.parking.domain.tenant;

import com.urbanpark.parking.domain.saas.plan.Plan;
import com.urbanpark.parking.domain.saas.plan.PlanRepository;
import com.urbanpark.parking.domain.saas.user.SaasUser;
import com.urbanpark.parking.domain.saas.user.SaasUserRepository;
import com.urbanpark.parking.domain.tenant.dto.CondominioRequest;
import com.urbanpark.parking.domain.tenant.dto.CondominioResponse;
import com.urbanpark.parking.shared.enums.EstadoCondominio;
import com.urbanpark.parking.shared.enums.RolSaas;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CondominioService {

    private final CondominioRepository condominioRepository;
    private final PlanRepository planRepository;
    private final SaasUserRepository saasUserRepository;
    private final PasswordEncoder passwordEncoder;

    public CondominioResponse crear(CondominioRequest request) {
        if (condominioRepository.existsByTitularEmail(request.getTitularEmail())) {
            throw new IllegalArgumentException("Ya existe un condominio con ese email de titular");
        }
        if (saasUserRepository.existsByEmail(request.getTitularEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado"));

        Condominio condominio = Condominio.builder()
                .nombre(request.getNombre())
                .apiBaseUrl(request.getApiBaseUrl())
                .titularNombre(request.getTitularNombre())
                .titularDni(request.getTitularDni())
                .titularEmail(request.getTitularEmail())
                .titularTelefono(request.getTitularTelefono())
                .estado(EstadoCondominio.ACTIVO)
                .plan(plan)
                .build();

        condominioRepository.save(condominio);

        SaasUser cliente = SaasUser.builder()
                .email(request.getTitularEmail())
                .password(passwordEncoder.encode(generarPasswordInicial(request)))
                .nombre(request.getTitularNombre())
                .dni(request.getTitularDni())
                .telefono(request.getTitularTelefono())
                .cargo("Titular de Condominio")
                .rol(RolSaas.CLIENTE)
                .activo(true)
                .esBase(false)
                .build();

        saasUserRepository.save(cliente);

        return toResponse(condominio);
    }

    public List<CondominioResponse> listarTodos() {
        return condominioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CondominioResponse buscarPorId(UUID id) {
        return toResponse(findById(id));
    }

    public CondominioResponse actualizar(UUID id, CondominioRequest request) {
        Condominio condominio = findById(id);
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado"));

        // Si cambia el email, verificar que no exista en otro condominio
        if (!condominio.getTitularEmail().equals(request.getTitularEmail())) {
            if (condominioRepository.existsByTitularEmail(request.getTitularEmail())) {
                throw new IllegalArgumentException("Ya existe un condominio con ese email de titular");
            }
        }

        // Actualizar DNI y email en el SaasUser CLIENTE asociado
        saasUserRepository.findByEmail(condominio.getTitularEmail())
                .ifPresent(cliente -> {
                    cliente.setNombre(request.getTitularNombre());
                    cliente.setDni(request.getTitularDni());
                    cliente.setEmail(request.getTitularEmail());
                    cliente.setTelefono(request.getTitularTelefono());
                    saasUserRepository.save(cliente);
                });

        condominio.setNombre(request.getNombre());
        condominio.setApiBaseUrl(request.getApiBaseUrl());
        condominio.setTitularNombre(request.getTitularNombre());
        condominio.setTitularDni(request.getTitularDni());
        condominio.setTitularEmail(request.getTitularEmail());
        condominio.setTitularTelefono(request.getTitularTelefono());
        condominio.setPlan(plan);

        return toResponse(condominioRepository.save(condominio));
    }

    public void cambiarEstado(UUID id, EstadoCondominio estado) {
        Condominio condominio = findById(id);
        condominio.setEstado(estado);
        condominioRepository.save(condominio);
    }

    public void eliminar(UUID id) {
        Condominio condominio = findById(id);

        // Eliminar el usuario CLIENTE asociado al titular
        saasUserRepository.findByEmail(condominio.getTitularEmail())
                .ifPresent(saasUserRepository::delete);

        condominioRepository.delete(condominio);
    }

    // ─── Helpers ─────────────────────────────────────────────────────

    private String generarPasswordInicial(CondominioRequest request) {
        String sinEspacios = request.getTitularNombre().replaceAll("\\s+", "");
        String prefijo = sinEspacios.substring(0, Math.min(4, sinEspacios.length()));
        int anio = java.time.LocalDate.now().getYear();
        return "Urban" + prefijo + "@" + anio;
    }

    private Condominio findById(UUID id) {
        return condominioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Condominio no encontrado"));
    }

    private CondominioResponse toResponse(Condominio c) {
        UUID clienteUserId = saasUserRepository.findByEmail(c.getTitularEmail())
                .map(SaasUser::getId)
                .orElse(null);

        return CondominioResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .apiBaseUrl(c.getApiBaseUrl())
                .titularNombre(c.getTitularNombre())
                .titularDni(c.getTitularDni())
                .titularEmail(c.getTitularEmail())
                .titularTelefono(c.getTitularTelefono())
                .planId(c.getPlan().getId())
                .planNombre(c.getPlan().getNombre())
                .estado(c.getEstado())
                .clienteUserId(clienteUserId)
                .fechaRegistro(c.getFechaRegistro())
                .build();
    }
}