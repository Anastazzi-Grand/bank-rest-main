package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Обработка кастомных исключений
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCardNotFound(CardNotFoundException ex) {
        ErrorResponseDto error = ErrorResponseDto.builder()
                .code("CARD_NOT_FOUND")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponseDto> handleInsufficientFunds(InsufficientFundsException ex) {
        ErrorResponseDto error = ErrorResponseDto.builder()
                .code("INSUFFICIENT_FUNDS")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CardActionNotAllowedException.class)
    public ResponseEntity<ErrorResponseDto> handleCardActionNotAllowed(CardActionNotAllowedException ex) {
        ErrorResponseDto error = ErrorResponseDto.builder()
                .code("CARD_ACTION_NOT_ALLOWED")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponseDto error = ErrorResponseDto.builder()
                .code("USER_NOT_FOUND")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TransferBetweenSameCardException.class)
    public ResponseEntity<ErrorResponseDto> handleTransferBetweenSameCard(TransferBetweenSameCardException ex) {
        ErrorResponseDto error = ErrorResponseDto.builder()
                .code("TRANSFER_TO_SAME_CARD")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Обработка ошибок валидации @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Обработка ошибок валидации для @Valid на уровне параметров
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Обработка любых других непредвиденных ошибок
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        ErrorResponseDto error = ErrorResponseDto.builder()
                .code("INTERNAL_ERROR")
                .message("An unexpected error occurred")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}