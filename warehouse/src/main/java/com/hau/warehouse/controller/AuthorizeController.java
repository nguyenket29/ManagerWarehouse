package com.hau.warehouse.controller;

import com.hau.warehouse.payload.request.LoginRequest;
import com.hau.warehouse.payload.request.TokenRefreshRequest;
import com.hau.warehouse.payload.request.UserRequest;
import com.hau.warehouse.service.IUserService;
import com.hau.warehouse.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    private final IUserService userService;

    public AuthorizeController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody LoginRequest request){
        return userService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> Register(@RequestBody UserRequest userRequest){
        return userService.register(userRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> RefreshToken(@RequestBody TokenRefreshRequest userRequest){
        return userService.TokenRefresh(userRequest);
    }
}
