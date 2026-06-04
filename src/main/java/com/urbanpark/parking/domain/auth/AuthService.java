package com.urbanpark.parking.domain.auth;

import com.urbanpark.parking.domain.audit.AuditLogService;
import com.urbanpark.parking.domain.auth.dto.*;
import com.urbanpark.parking.domain.auth.validators.RegisterValidator;
import com.urbanpark.parking.domain.usuarios.UsuarioSaas;
import com.urbanpark.parking.domain.usuarios.UsuarioSaasRepository;
import com.urbanpark.parking.security.jwt.JwtService;
import com.urbanpark.parking.security.otp.OtpService;
import com.urbanpark.parking.shared.audit.AuditableAction;
import com.urbanpark.parking.shared.enums.*;
import com.urbanpark.parking.shared.exceptions.AccesoDenegadoException;
import com.urbanpark.parking.shared.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioSaasRepository usuarioSaasRepository;
    private final RegisterValidator     registerValidator;
    private final PasswordEncoder       passwordEncoder;
    private final JwtService            jwtService;
    private final OtpService            otpService;
    private final AuthenticationManager authenticationManager;
    private final AuditLogService       auditLogService;
    private final HttpServletRequest    httpRequest;

    @Transactional
    @AuditableAction(
            accion      = TipoAccionAudit.USUARIO_CREADO,
            descripcion = "Registro público de nuevo cliente",
            entidad     = "UsuarioSaas"
    )
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

    @AuditableAction(
            accion      = TipoAccionAudit.LOGIN,
            descripcion = "Inicio de sesión exitoso",
            entidad     = "UsuarioSaas"
    )
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            auditLogService.registrar(
                    null,
                    request.getEmail(),
                    "N/A",
                    TipoAccionAudit.LOGIN_FALLIDO,
                    "Credenciales inválidas",
                    "UsuarioSaas",
                    httpRequest.getRequestURI(),
                    httpRequest.getMethod(),
                    resolverIp(),
                    false,
                    ex.getMessage()
            );
            throw ex;
        }

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

    @AuditableAction(
            accion      = TipoAccionAudit.USUARIO_ACTUALIZADO,
            descripcion = "Solicitud de recuperación de contraseña",
            entidad     = "UsuarioSaas"
    )
    public void forgotPassword(ForgotPasswordRequest request) {
        UsuarioSaas usuario = usuarioSaasRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe cuenta con ese email"));
        otpService.generarYEnviar(usuario);
    }

    @Transactional
    @AuditableAction(
            accion      = TipoAccionAudit.USUARIO_ACTUALIZADO,
            descripcion = "Contraseña restablecida exitosamente",
            entidad     = "UsuarioSaas"
    )
    public void resetPassword(ResetPasswordRequest request) {
        UsuarioSaas usuario = usuarioSaasRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe cuenta con ese email"));
        otpService.validar(usuario, request.getOtp());
        usuario.setPasswordHash(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioSaasRepository.save(usuario);
    }

    private String resolverIp() {
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = httpRequest.getRemoteAddr();
        return ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }
}