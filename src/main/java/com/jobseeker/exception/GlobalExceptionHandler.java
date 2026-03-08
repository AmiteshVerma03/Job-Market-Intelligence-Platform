package com.jobseeker.exception;

import java.util.Map;

import org.jsoup.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<Map<String, Object>> handleUpstreamHttpStatus(HttpStatusException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(Map.of(
                        "status", "error",
                        "message", "Upstream site blocked the scraper request",
                        "upstreamStatus", ex.getStatusCode(),
                        "url", ex.getUrl()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
