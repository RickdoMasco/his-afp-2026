package com.sio.backend.exception;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sio.backend.generics.ApiResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the SIO application.
 * Intercepts exceptions raised by the controllers and wraps them
 * into standardized HTTP responses through {@link ApiResponse}.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * General fallback for any unexpected exception. HTTP 500.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleAll(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        return ApiResponse.error(
                "Unexpected server error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "THX403");
    }

    /**
     * Invalid arguments or malformed requests. HTTP 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        ApiResponse<Void> body = ApiResponse.fail(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /**
     * JPA entity not found. HTTP 404.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleEntityNotFound(EntityNotFoundException ex) {
        return ApiResponse.fail(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value());
    }

    /**
     * Missing element. HTTP 404.
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNoSuchElement(NoSuchElementException ex) {
        return ApiResponse.fail(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value());
    }

    /**
     * DTO validation errors. HTTP 400, with the invalid fields aggregated.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        String fields = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce((s1, s2) -> s1 + "; " + s2)
                .orElse("Validation error");

        return ApiResponse.fail(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                fields);
    }

    /**
     * Unreadable request body or unparsable JSON. HTTP 400.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return ApiResponse.fail(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Database transaction level errors. HTTP 400.
     */
    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleTransaction(TransactionSystemException ex) {
        return ApiResponse.fail(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value());
    }
}
