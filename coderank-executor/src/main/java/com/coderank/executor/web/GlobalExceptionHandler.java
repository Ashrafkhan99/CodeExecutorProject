package com.coderank.executor.web;

import com.coderank.executor.execute.SystemBusyException;
import com.coderank.executor.execute.TooManyInFlightException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        return json(HttpStatus.BAD_REQUEST, "validation_error", "Invalid request payload", req.getRequestURI(),
                Map.of("fields", fieldErrors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String,Object>> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        return json(HttpStatus.BAD_REQUEST, "validation_error", ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleIllegal(IllegalArgumentException ex, HttpServletRequest req) {
        return json(HttpStatus.BAD_REQUEST, "bad_request", ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> handleDenied(AccessDeniedException ex, HttpServletRequest req) {
        return json(HttpStatus.FORBIDDEN, "forbidden", "You do not have permission to perform this action", req.getRequestURI(), null);
    }

    @ExceptionHandler(TooManyInFlightException.class)
    public ResponseEntity<Map<String,Object>> handleInFlight(TooManyInFlightException ex) {
        Map<String,Object> body = new HashMap<>();
        body.put("error", "too_many_requests");
        body.put("message", ex.getMessage());
        body.put("reason", "per_user");
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Exec-Max", String.valueOf(ex.getLimit()));
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).headers(headers).body(body);
    }

    @ExceptionHandler(SystemBusyException.class)
    public ResponseEntity<Map<String,Object>> handleBusy(SystemBusyException ex) {
        Map<String,Object> body = new HashMap<>();
        body.put("error", "too_many_requests");
        body.put("message", ex.getMessage());
        body.put("reason", ex.getReason());
        HttpHeaders headers = new HttpHeaders();
        if (ex.getQueueSize() != null) headers.add("X-Queue-Size", ex.getQueueSize().toString());
        if (ex.getMaxConcurrent() != null) headers.add("X-Exec-Max", ex.getMaxConcurrent().toString());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).headers(headers).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGeneric(Exception ex, HttpServletRequest req) {
        return json(HttpStatus.INTERNAL_SERVER_ERROR, "internal_error", ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                req.getRequestURI(), null);
    }

    private ResponseEntity<Map<String,Object>> json(HttpStatus status, String error, String message, String path, Map<String,?> extra) {
        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        if (extra != null) body.putAll(extra);
        return ResponseEntity.status(status).body(body);
    }
}
