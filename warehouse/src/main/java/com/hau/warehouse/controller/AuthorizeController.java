package com.hau.warehouse.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pages")
public class AuthorizeController {
    @GetMapping("/employee")
    @PreAuthorize("hasRole('ROLE_EMPLOYEE') or hasRole('ROLE_MANAGER')")
    public String employeeAccess() {
        return "Employee Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
