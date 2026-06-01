package com.urbanpark.parking.user.service;
 
import com.urbanpark.parking.user.domain.model.User;
import com.urbanpark.parking.user.dto.request.UpdateUserRequest;
import com.urbanpark.parking.user.dto.response.UserResponse;
 
import java.util.List;
 
public interface UserService {
 
    /** Retorna todos los usuarios del condominio (tenant) actual. */
    List<UserResponse> getAllUsers(String tenantId);
 
    /** Busca un usuario por su ID interno del SaaS. */
    UserResponse getUserById(Long id, String tenantId);
 
    /** Busca un usuario por su ID externo del condominio. */
    UserResponse getUserByExternalId(String externalId, String tenantId);
 
    /** Actualiza campos editables de un usuario. */
    UserResponse updateUser(Long id, UpdateUserRequest request, String tenantId);
 
    /** Desactiva un usuario sin eliminarlo (soft delete). */
    void deactivateUser(Long id, String tenantId);
 
    /**
     * Sincroniza usuarios desde la API externa del condominio.
     * Crea usuarios nuevos y actualiza existentes. (RF-05)
     *
     * @param tenantId   ID del condominio
     * @param externalJwt JWT del sistema externo del condominio
     */
    int syncUsersFromCondominium(String tenantId, String externalJwt);
 
    /**
     * Crea o actualiza un usuario a partir de datos del sistema externo.
     * Usado durante el flujo de autenticación (RF-04).
     */
    User createOrUpdateFromExternal(
        String externalId,
        String tenantId,
        String name,
        String email,
        String phoneNumber,
        String role,
        String apartmentNumber,
        String externalApartmentId
    );
}