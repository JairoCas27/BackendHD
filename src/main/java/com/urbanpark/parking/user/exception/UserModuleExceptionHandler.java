package com.urbanpark.parking.user.exception;
 
import com.urbanpark.parking.vehicle.exception.VehicleNotFoundException;
import com.urbanpark.parking.visitor.exception.VisitorNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
 
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
 
/**
 * Manejador de excepciones específico del módulo de usuarios (feature/users).
 */
@RestControllerAdvice
@Order(1)
public class UserModuleExceptionHandler {
 
    // ──────────────────────────────────────────────────────────────────────────
    // 404 - Tipos específicos de feature/users
    // ──────────────────────────────────────────────────────────────────────────
 
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }
 
    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<Object> handleVehicleNotFound(VehicleNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }
 
    @ExceptionHandler(VisitorNotFoundException.class)
    public ResponseEntity<Object> handleVisitorNotFound(VisitorNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // 400 - Validaciones de @Valid en los controllers de este módulo
    // ──────────────────────────────────────────────────────────────────────────
 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
 
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Error");
        body.put("message", "Datos inválidos en la solicitud.");
        body.put("details", details);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // HELPER
    // ──────────────────────────────────────────────────────────────────────────
 
    private ResponseEntity<Object> buildResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}