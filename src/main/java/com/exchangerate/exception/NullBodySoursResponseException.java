package com.exchangerate.exception;

public class NullBodySoursResponseException extends RuntimeException {

    public NullBodySoursResponseException() {
        super("Missing response body from https://openexchangerates.org.");
    }
}
