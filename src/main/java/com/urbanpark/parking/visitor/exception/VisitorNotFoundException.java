package com.urbanpark.parking.visitor.exception;
 
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
 
@ResponseStatus(HttpStatus.NOT_FOUND)
public class VisitorNotFoundException extends RuntimeException {
    public VisitorNotFoundException(Long id) {
        super("Visitante no encontrado con id: " + id);
    }
}
 