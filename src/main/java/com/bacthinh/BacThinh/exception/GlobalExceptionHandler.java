package com.bacthinh.BacThinh.exception;

import com.bacthinh.BacThinh.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = "Invalid JSON format";

        // Xử lý lỗi enum parsing
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) ex.getCause();
            String fieldName = invalidFormatException.getPathReference();
            String invalidValue = invalidFormatException.getValue().toString();
            String targetType = invalidFormatException.getTargetType().getSimpleName();

            if (targetType.equals("UserRole")) {
                errorMessage = String.format("Invalid role value '%s'. Valid values are: ADMIN, CHA_XU, GIAO_DAN, TRUONG_HOI", invalidValue);
            } else if (targetType.equals("UserStatus")) {
                errorMessage = String.format("Invalid status value '%s'. Valid values are: ACTIVE, INACTIVE, BLOCKED, PENDING", invalidValue);
            } else if (targetType.equals("Gender")) {
                errorMessage = String.format("Invalid gender value '%s'. Valid values are: MALE, FEMALE", invalidValue);
            } else if (targetType.equals("RelationToHead")) {
                errorMessage = String.format("Invalid relation value '%s'. Valid values are: CHA, ME, CON, ONG, BA, KHAC", invalidValue);
            } else {
                errorMessage = String.format("Invalid value '%s' for field %s", invalidValue, fieldName);
            }
        }

        log.error("JSON parsing error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errorMessage, 400));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation error: {}", errors);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Validation failed", errors, 400));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), 404));
    }

    @ExceptionHandler(UserExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiResponse<String>> handleUserExistException(UserExistException ex) {
        log.error("User already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("User already exists: " + ex.getMessage(), 409));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error", 500));
    }

    @ExceptionHandler(TokenException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse<Void>> handleTokenException(TokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }
} 