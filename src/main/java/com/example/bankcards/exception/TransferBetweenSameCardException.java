package com.example.bankcards.exception;

public class TransferBetweenSameCardException extends RuntimeException {
    public TransferBetweenSameCardException(String message) {
        super(message);
    }
}