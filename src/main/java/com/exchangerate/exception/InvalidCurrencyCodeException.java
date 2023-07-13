package com.exchangerate.exception;

public class InvalidCurrencyCodeException extends RuntimeException {

    public InvalidCurrencyCodeException() {
        super("Not an existing currency code.");
    }
}
