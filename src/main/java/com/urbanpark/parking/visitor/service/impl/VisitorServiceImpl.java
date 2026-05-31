package com.urbanpark.parking.visitor.service.impl;
 
import com.urbanpark.parking.core.exception.BusinessException;
import com.urbanpark.parking.user.domain.model.User;
import com.urbanpark.parking.user.domain.model.Visitor;
import com.urbanpark.parking.user.exception.UserNotFoundException;
import com.urbanpark.parking.user.repository.UserRepository;
import com.urbanpark.parking.visitor.dto.request.CreateVisitorRequest;
import com.urbanpark.parking.visitor.dto.response.VisitorResponse;
import com.urbanpark.parking.visitor.mapper.VisitorMapper;
import com.urbanpark.parking.visitor.repository.VisitorRepository;
import com.urbanpark.parking.visitor.service.VisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.List;
 
@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {
 
    private final VisitorRepository visitorRepository;
    private final UserRepository userRepository;
    private final VisitorMapper visitorMapper;
 
    @Override
    @Transactional
    public VisitorResponse registerVisitor(Long userId, CreateVisitorRequest request, String tenantId) {
        User authorizedBy = userRepository.findByIdAndTenantId(userId, tenantId)
            .orElseThrow(() -> new UserNotFoundException(userId));
 
        // Validamos que las fechas sean coherentes
        if (request.validUntil().isBefore(request.validFrom())) {
            throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio.");
        }
 
        if (request.validFrom().isAfter(request.validUntil())) {
            throw new BusinessException("El período de visita no es válido.");
        }
 
        Visitor visitor = Visitor.builder()
            .tenantId(tenantId)
            .name(request.name())
            .idDocument(request.idDocument())
            .vehiclePlate(request.vehiclePlate() != null
                ? request.vehiclePlate().toUpperCase().trim()
                : null)
            .vehicleDescription(request.vehicleDescription())
            .authorizedBy(authorizedBy)
            .validFrom(request.validFrom())
            .validUntil(request.validUntil())
            .isActive(true)
            .notes(request.notes())
            .build();
 
        VisitorResponse saved = visitorMapper.toResponse(visitorRepository.save(visitor));
        log.info("Visitante registrado: {} para usuario {} en tenant {}", request.name(), userId, tenantId);
        return saved;
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<VisitorResponse> getVisitorsByUser(Long userId, String tenantId) {
        return visitorRepository.findAllByAuthorizedByIdAndTenantId(userId, tenantId)
            .stream()
            .map(visitorMapper::toResponse)
            .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<VisitorResponse> getActiveVisitors(String tenantId) {
        LocalDateTime now = LocalDateTime.now();
        return visitorRepository.findAllByTenantId(tenantId)
            .stream()
            .filter(v -> Boolean.TRUE.equals(v.getIsActive()) && v.getValidUntil().isAfter(now))
            .map(visitorMapper::toResponse)
            .toList();
    }
 
    @Override
    @Transactional(readOnly = true)
    public VisitorResponse getVisitorById(Long id, String tenantId) {
        Visitor visitor = visitorRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new BusinessException("Visitante no encontrado con id: " + id));
        return visitorMapper.toResponse(visitor);
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<VisitorResponse> getActiveVisitorsByPlate(String plate, String tenantId) {
        String normalizedPlate = plate.toUpperCase().trim();
        return visitorRepository.findActiveVisitorsByPlate(tenantId, normalizedPlate, LocalDateTime.now())
            .stream()
            .map(visitorMapper::toResponse)
            .toList();
    }
 
    @Override
    @Transactional
    public void revokeVisitor(Long id, String tenantId) {
        Visitor visitor = visitorRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new BusinessException("Visitante no encontrado con id: " + id));
        visitor.setIsActive(false);
        visitorRepository.save(visitor);
        log.info("Visitante {} revocado en tenant {}", id, tenantId);
    }
}