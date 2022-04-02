package com.hau.warehouse.service;

import com.hau.warehouse.payload.request.LoginRequest;
import com.hau.warehouse.payload.request.TokenRefreshRequest;
import com.hau.warehouse.payload.request.UserRequest;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    ResponseEntity<?> login(LoginRequest request);
    ResponseEntity<?> register(UserRequest userRequest);
    ResponseEntity<?> TokenRefresh(TokenRefreshRequest tokenRequest);
}
