package com.exchangerate.exception;

public class OpenExchangeRatesNullBodySoursResponseException extends RuntimeException {

    public OpenExchangeRatesNullBodySoursResponseException() {
        super("Missing response body from https://openexchangerates.org.");
    }
}
