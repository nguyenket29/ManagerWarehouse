package com.hau.warehouse.exception;

public class CustomParameterException extends RuntimeException {
    public CustomParameterException() {
        super();
    }

    public CustomParameterException(String message) {
        super(message);
    }
}
