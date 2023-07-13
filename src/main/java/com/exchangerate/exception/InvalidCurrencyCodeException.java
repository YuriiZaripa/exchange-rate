package com.exchangerate.exception;

public class InvalidCurrencyCodeException extends RuntimeException {

    public InvalidCurrencyCodeException(String code) {
        super("Currency code doesn't exist: " + code);
    }
}
