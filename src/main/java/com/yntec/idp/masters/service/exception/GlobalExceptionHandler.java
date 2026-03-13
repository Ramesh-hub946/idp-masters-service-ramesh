package com.yntec.idp.masters.service.exception;

import com.yntec.idp.masters.service.payload.response.ApiResponse;
import com.yntec.idp.masters.service.util.ResponseBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationMasterNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(LocationMasterNotFoundException ex) {

        ApiResponse<Object> response =
                ResponseBuilder.error(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        ex.getMessage()
                );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        ApiResponse<Object> response =
                ResponseBuilder.error(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        errors
                );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {

        ApiResponse<Object> response =
                ResponseBuilder.error(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        ex.getMessage()
                );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrity(DataIntegrityViolationException ex) {

        ApiResponse<Object> response =
                ResponseBuilder.error(
                        HttpStatus.CONFLICT.value(),
                        "Duplicate or invalid data",
                        "Database constraint violated"
                );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {

        ApiResponse<Object> response =
                ResponseBuilder.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal server error",
                        ex.getMessage()
                );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
