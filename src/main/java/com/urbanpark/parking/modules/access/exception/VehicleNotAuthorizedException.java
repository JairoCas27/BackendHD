package com.urbanpark.parking.modules.access.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class VehicleNotAuthorizedException extends RuntimeException {
    public VehicleNotAuthorizedException(String message) {
        super(message);
    }
}