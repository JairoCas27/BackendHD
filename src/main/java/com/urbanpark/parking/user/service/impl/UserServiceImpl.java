package com.urbanpark.parking.user.service.impl;
 
import com.urbanpark.parking.integration.client.CondominiumApiClient;
import com.urbanpark.parking.integration.dto.ExternalCondominiumDtos.ExternalUserDto;
import com.urbanpark.parking.user.domain.enums.UserRole;
import com.urbanpark.parking.user.domain.enums.UserStatus;
import com.urbanpark.parking.user.domain.model.User;
import com.urbanpark.parking.user.dto.request.UpdateUserRequest;
import com.urbanpark.parking.user.dto.response.UserResponse;
import com.urbanpark.parking.user.exception.UserNotFoundException;
import com.urbanpark.parking.user.mapper.UserMapper;
import com.urbanpark.parking.user.repository.UserRepository;
import com.urbanpark.parking.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.List;
 
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
 
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CondominiumApiClient condominiumApiClient;
 
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(String tenantId) {
        return userRepository.findAllByTenantId(tenantId)
            .stream()
            .map(userMapper::toResponse)
            .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id, String tenantId) {
        User user = findUserOrThrow(id, tenantId);
        return userMapper.toResponse(user);
    }
 
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByExternalId(String externalId, String tenantId) {
        User user = userRepository.findByExternalIdAndTenantId(externalId, tenantId)
            .orElseThrow(() -> new UserNotFoundException(externalId, tenantId));
        return userMapper.toResponse(user);
    }
 
    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request, String tenantId) {
        User user = findUserOrThrow(id, tenantId);
 
        if (request.name() != null) user.setName(request.name());
        if (request.email() != null) user.setEmail(request.email());
        if (request.phoneNumber() != null) user.setPhoneNumber(request.phoneNumber());
        if (request.status() != null) user.setStatus(request.status());
 
        return userMapper.toResponse(userRepository.save(user));
    }
 
    @Override
    @Transactional
    public void deactivateUser(Long id, String tenantId) {
        User user = findUserOrThrow(id, tenantId);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        log.info("Usuario {} desactivado en tenant {}", id, tenantId);
    }
 
    @Override
    @Transactional
    public int syncUsersFromCondominium(String tenantId, String externalJwt) {
        log.info("Iniciando sincronización de usuarios para tenant: {}", tenantId);
 
        List<ExternalUserDto> externalUsers = condominiumApiClient.getAllUsers(externalJwt);
        int synced = 0;
 
        for (ExternalUserDto dto : externalUsers) {
            try {
                createOrUpdateFromExternal(
                    dto.id(),
                    tenantId,
                    dto.name(),
                    dto.email(),
                    dto.phoneNumber(),
                    dto.role(),
                    dto.apartmentNumber(),
                    dto.apartmentId()
                );
                synced++;
            } catch (Exception e) {
                log.error("Error sincronizando usuario externo {} en tenant {}: {}",
                    dto.id(), tenantId, e.getMessage());
            }
        }
 
        log.info("Sincronización completada para tenant {}. Usuarios procesados: {}/{}", 
            tenantId, synced, externalUsers.size());
        return synced;
    }
 
    @Override
    @Transactional
    public User createOrUpdateFromExternal(
        String externalId,
        String tenantId,
        String name,
        String email,
        String phoneNumber,
        String roleName,
        String apartmentNumber,
        String externalApartmentId
    ) {
        UserRole role = mapExternalRole(roleName);
 
        return userRepository.findByExternalIdAndTenantId(externalId, tenantId)
            .map(existing -> {
                // Actualizar datos del usuario existente
                existing.setName(name);
                existing.setEmail(email);
                existing.setPhoneNumber(phoneNumber);
                existing.setRole(role);
                existing.setApartmentNumber(apartmentNumber);
                existing.setExternalApartmentId(externalApartmentId);
                existing.setLastSyncedAt(LocalDateTime.now());
                return userRepository.save(existing);
            })
            .orElseGet(() -> {
                // Crear nuevo usuario en el SaaS
                User newUser = User.builder()
                    .externalId(externalId)
                    .tenantId(tenantId)
                    .name(name)
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .role(role)
                    .apartmentNumber(apartmentNumber)
                    .externalApartmentId(externalApartmentId)
                    .status(UserStatus.ACTIVE)
                    .lastSyncedAt(LocalDateTime.now())
                    .build();
                log.info("Nuevo usuario creado en SaaS: externalId={}, tenant={}", externalId, tenantId);
                return userRepository.save(newUser);
            });
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ──────────────────────────────────────────────────────────────────────────
 
    private User findUserOrThrow(Long id, String tenantId) {
        return userRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
 
    /**
     * Mapea el nombre de rol del sistema externo al rol interno del SaaS.
     * Tolerante a variaciones de capitalización.
     */
    private UserRole mapExternalRole(String externalRole) {
        if (externalRole == null) return UserRole.PROPIETARIO;
        return switch (externalRole.toUpperCase().trim()) {
            case "ADMIN", "ADMINISTRADOR", "ADMIN_CONDOMINIO" -> UserRole.ADMIN_CONDOMINIO;
            case "SEGURIDAD", "AGENTE", "AGENTE_SEGURIDAD", "GUARD" -> UserRole.AGENTE_SEGURIDAD;
            default -> UserRole.PROPIETARIO;
        };
    }
}