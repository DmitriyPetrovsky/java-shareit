package ru.practicum.shareit.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(BindException e) {
        FieldError err = e.getFieldErrors().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Validation error without field errors"));
        String fieldName = err.getField();
        String errorMessage = err.getDefaultMessage();
        ErrorResponse error = new ErrorResponse(String.format("Поле %s %s", fieldName, errorMessage));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}
