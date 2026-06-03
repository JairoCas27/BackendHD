package com.urbanpark.parking.domain.auth;

import com.urbanpark.parking.domain.auth.dto.*;
import com.urbanpark.parking.domain.auth.validators.RegisterValidator;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasRepository;
import com.urbanpark.parking.security.jwt.JwtService;
import com.urbanpark.parking.security.otp.OtpService;
import com.urbanpark.parking.shared.enums.EstadoUsuarioSaas;
import com.urbanpark.parking.shared.enums.OrigenRegistro;
import com.urbanpark.parking.shared.enums.RolSaas;
import com.urbanpark.parking.shared.exceptions.AccesoDenegadoException;
import com.urbanpark.parking.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioSaasRepository usuarioSaasRepository;
    private final RegisterValidator registerValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void register(RegisterRequest request) {
        registerValidator.validate(request);

        UsuarioSaas usuario = UsuarioSaas.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .dni(request.getDni())
                .telefono(request.getTelefono())
                .rol(RolSaas.CLIENTE)
                .estado(EstadoUsuarioSaas.PENDIENTE_PLAN)
                .origenRegistro(OrigenRegistro.PUBLICO)
                .creadoPor(null)
                .esBaseProtegido(false)
                .build();

        usuarioSaasRepository.save(usuario);
    }

    public LoginResponse login(LoginRequest request) {
        // Spring Security valida credenciales
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UsuarioSaas usuario = usuarioSaasRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (usuario.getEstado() == EstadoUsuarioSaas.SUSPENDIDO)
            throw new AccesoDenegadoException("Tu cuenta está suspendida");

        if (usuario.getEstado() == EstadoUsuarioSaas.INACTIVO)
            throw new AccesoDenegadoException("Tu cuenta está inactiva");

        return LoginResponse.builder()
                .token(jwtService.generateToken(usuario))
                .refreshToken(jwtService.generateRefreshToken(usuario))
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .estado(usuario.getEstado())
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        UsuarioSaas usuario = usuarioSaasRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No existe cuenta con ese email"));

        otpService.generarYEnviar(usuario);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        UsuarioSaas usuario = usuarioSaasRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No existe cuenta con ese email"));

        // Valida OTP (lanza excepción si es inválido o expirado)
        otpService.validar(usuario, request.getOtp());

        usuario.setPasswordHash(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioSaasRepository.save(usuario);
    }
}