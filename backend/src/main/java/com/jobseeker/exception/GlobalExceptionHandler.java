package com.jobseeker.exception;

import java.util.Map;
import java.util.LinkedHashMap;

import org.jsoup.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Unified error response shape — all errors return the same structure
    private Map<String, Object> errorBody(int code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "error");
        body.put("code", code);
        body.put("message", message);
        return body;
    }

    // Handles @Valid failures on DTOs — returns field-level messages
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorBody(400, message));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(errorBody(ex.getStatusCode().value(), ex.getReason()));
    }

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<Map<String, Object>> handleUpstreamHttpStatus(HttpStatusException ex) {
        Map<String, Object> body = errorBody(502, "Upstream site blocked the scraper request");
        body.put("upstreamStatus", ex.getStatusCode());
        body.put("url", ex.getUrl());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorBody(400, ex.getMessage()));
    }
}
