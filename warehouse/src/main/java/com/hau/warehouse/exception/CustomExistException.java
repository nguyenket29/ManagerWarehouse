package com.hau.warehouse.exception;

public class CustomExistException extends RuntimeException {
	public CustomExistException() {
        super();
    }


    public CustomExistException(String message) {
        super(message);
    }


    public CustomExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
