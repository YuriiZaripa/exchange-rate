package com.exchangerate.exception;

public class OpenExchangeRatesNullBodySourceResponseException extends RuntimeException {

    public OpenExchangeRatesNullBodySourceResponseException() {
        super("Missing response body from https://openexchangerates.org.");
    }
}
