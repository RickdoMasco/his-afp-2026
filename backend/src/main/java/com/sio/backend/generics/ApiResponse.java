package com.sio.backend.generics;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standardized wrapper for every REST response of the SIO application.
 * Carries the outcome, the temporal metadata and the data or error payload.
 *
 * @param <T> type of the payload carried by the response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard wrapper for SIO API responses")
public class ApiResponse<T> {

    @Schema(description = "Outcome status of the request (SUCCESS, FAIL, ERROR)", example = "SUCCESS")
    private ApiResponseStatus status;

    @Schema(description = "HTTP status code associated with the response", example = "200")
    private Integer statusCode;

    @Schema(description = "Timestamp when the response was generated")
    private Instant timestamp;

    @Schema(description = "Payload returned by the operation")
    private T data;

    @Schema(description = "Descriptive message for the outcome or the error", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Custom internal application error code", example = "THX403")
    private String errorCode;

    @Schema(description = "Error stack trace (only meant for debug/development environments)")
    private String stackTrace;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(ApiResponseStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .timestamp(Instant.now())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, Integer statusCode, String errorCode) {
        return ApiResponse.<T>builder()
                .status(ApiResponseStatus.ERROR)
                .statusCode(statusCode)
                .errorCode(errorCode)
                .timestamp(Instant.now())
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, Integer statusCode, String errorCode, String stackTrace) {
        return ApiResponse.<T>builder()
                .status(ApiResponseStatus.ERROR)
                .statusCode(statusCode)
                .errorCode(errorCode)
                .timestamp(Instant.now())
                .message(message)
                .stackTrace(stackTrace)
                .build();
    }

    public static <T> ApiResponse<T> fail(String message, Integer statusCode) {
        return ApiResponse.<T>builder()
                .status(ApiResponseStatus.FAIL)
                .statusCode(statusCode)
                .timestamp(Instant.now())
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> fail(String message, Integer statusCode, String errorCode) {
        return ApiResponse.<T>builder()
                .status(ApiResponseStatus.FAIL)
                .statusCode(statusCode)
                .timestamp(Instant.now())
                .message(message)
                .errorCode(errorCode)
                .build();
    }
}
