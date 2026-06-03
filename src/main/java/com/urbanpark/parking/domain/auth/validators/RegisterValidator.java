package com.urbanpark.parking.domain.auth.validators;

import com.urbanpark.parking.domain.auth.dto.RegisterRequest;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasRepository;
import com.urbanpark.parking.shared.exceptions.ValidacionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterValidator {

    private final UsuarioSaasRepository usuarioSaasRepository;

    public void validate(RegisterRequest request) {
        if (usuarioSaasRepository.existsByEmail(request.getEmail()))
            throw new ValidacionException("Ya existe una cuenta con el email: " + request.getEmail());

        if (usuarioSaasRepository.existsByDni(request.getDni()))
            throw new ValidacionException("Ya existe una cuenta con el DNI: " + request.getDni());
    }
}