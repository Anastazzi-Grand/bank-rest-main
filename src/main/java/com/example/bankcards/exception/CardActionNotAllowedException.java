package com.example.bankcards.exception;

public class CardActionNotAllowedException extends RuntimeException {
    public CardActionNotAllowedException(String message) {
        super(message);
    }
}
