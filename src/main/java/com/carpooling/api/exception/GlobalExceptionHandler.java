package com.carpooling.api.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Une erreur est survenue: ", ex);
        ErrorResponse error = new ErrorResponse(
            "Une erreur est survenue lors du traitement de la requête",
            ex.getMessage()
        );
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Une erreur runtime est survenue: ", ex);
        ErrorResponse error = new ErrorResponse(
            "Une erreur est survenue lors du traitement de la requête",
            ex.getMessage()
        );
        return ResponseEntity.internalServerError().body(error);
    }
} 