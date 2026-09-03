package com.pulseride.driver.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException exception, WebRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> badRequest(RuntimeException exception, WebRequest request) {
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", exception.getMessage(), request);
    }

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(DriverNotFoundException exception, WebRequest request) {
        return error(HttpStatus.NOT_FOUND, "DRIVER_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> denied(AccessDeniedException exception, WebRequest request) {
        return error(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "Driver access is required", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> unauthenticated(AuthenticationException exception, WebRequest request) {
        return error(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "Authentication is required", request);
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String code, String message, WebRequest request) {
        String path = request instanceof ServletWebRequest servletRequest
                ? servletRequest.getRequest().getRequestURI() : "";
        return ResponseEntity.status(status).body(new ErrorResponse(Instant.now(), status.value(), code, message, path));
    }

    public record ErrorResponse(Instant timestamp, int status, String error, String message, String path) { }
}