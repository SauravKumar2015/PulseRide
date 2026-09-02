package com.pulseride.auth.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
                return error(HttpStatus.CONFLICT, "DUPLICATE_USER", ex.getMessage(), request);
        }

        @ExceptionHandler({BadCredentialsException.class, InvalidRefreshTokenException.class})
        public ResponseEntity<ErrorResponse> handleAuthenticationFailure(RuntimeException ex, HttpServletRequest request) {
                return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication failed", request);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
                return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", request);
        }

        private ResponseEntity<ErrorResponse> error(HttpStatus status, String code, String message,
                                                                                                HttpServletRequest request) {
                return ResponseEntity.status(status)
                                .body(new ErrorResponse(Instant.now(), status.value(), code, message, request.getRequestURI()));
    }
}
