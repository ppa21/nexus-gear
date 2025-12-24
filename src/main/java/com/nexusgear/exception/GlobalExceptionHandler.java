package com.nexusgear.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Catches the Concurrency Error
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleConcurrency(ObjectOptimisticLockingFailureException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Item was just sold to someone else. Please refresh.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
