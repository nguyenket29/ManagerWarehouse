package com.hau.warehouse.exception;

public class CustomTokenRefreshException extends RuntimeException{
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CustomTokenRefreshException(String token, String message) {
        super(message);
        this.token = token;
    }
}
