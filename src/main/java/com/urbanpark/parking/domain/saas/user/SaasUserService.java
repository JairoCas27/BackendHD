package com.urbanpark.parking.domain.saas.user;

import com.urbanpark.parking.domain.saas.user.dto.SaasUserRequestDTO;
import com.urbanpark.parking.domain.saas.user.dto.SaasUserResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaasUserService {

    private final SaasUserRepository saasUserRepository;
    private final PasswordEncoder passwordEncoder;

    public List<SaasUserResponseDTO> listarTodos() {
        return saasUserRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SaasUserResponseDTO crear(SaasUserRequestDTO request) {
        if (saasUserRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con el email: " + request.getEmail());
        }

        SaasUser user = SaasUser.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .rol(request.getRol())
                .activo(true)
                .build();

        return toResponse(saasUserRepository.save(user));
    }

    public void desactivar(UUID id) {
        SaasUser user = findById(id);
        user.setActivo(false);
        saasUserRepository.save(user);
    }

    public void activar(UUID id) {
        SaasUser user = findById(id);
        user.setActivo(true);
        saasUserRepository.save(user);
    }

    public SaasUser findById(UUID id) {
        return saasUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario SaaS no encontrado"));
    }

    public SaasUser findByEmail(String email) {
        return saasUserRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario SaaS no encontrado: " + email));
    }

    private SaasUserResponseDTO toResponse(SaasUser user) {
        return SaasUserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nombre(user.getNombre())
                .rol(user.getRol().name())
                .activo(user.isActivo())
                .createdAt(user.getCreatedAt())
                .build();
    }
}