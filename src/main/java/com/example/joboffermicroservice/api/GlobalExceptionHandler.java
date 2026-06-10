package com.example.joboffermicroservice.api;

import com.example.joboffermicroservice.domain.IllegalStateTransitionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransition(IllegalStateTransitionException ex) {
        return ResponseEntity.status(409).body(new ErrorResponse(ex.getMessage()));
    }
}
