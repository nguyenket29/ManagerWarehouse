package com.hau.warehouse.payload.request;

public class LogoutRequest {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LogoutRequest(String token) {
        this.token = token;
    }
}
