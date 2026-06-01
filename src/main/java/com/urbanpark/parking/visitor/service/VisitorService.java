package com.urbanpark.parking.visitor.service;
 
import com.urbanpark.parking.visitor.dto.request.CreateVisitorRequest;
import com.urbanpark.parking.visitor.dto.response.VisitorResponse;
 
import java.util.List;
 
public interface VisitorService {
 
    /** Registra un visitante para un usuario propietario/inquilino. */
    VisitorResponse registerVisitor(Long userId, CreateVisitorRequest request, String tenantId);
 
    /** Lista todos los visitantes autorizados por un usuario. */
    List<VisitorResponse> getVisitorsByUser(Long userId, String tenantId);
 
    /** Lista todos los visitantes actualmente válidos en el condominio. */
    List<VisitorResponse> getActiveVisitors(String tenantId);
 
    /** Obtiene un visitante por ID. */
    VisitorResponse getVisitorById(Long id, String tenantId);
 
    /** Busca visitantes activos por placa (para control de acceso). */
    List<VisitorResponse> getActiveVisitorsByPlate(String plate, String tenantId);
 
    /** Revoca un visitante (lo desactiva antes de su expiración). */
    void revokeVisitor(Long id, String tenantId);
}
