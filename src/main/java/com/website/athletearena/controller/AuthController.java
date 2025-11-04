package com.website.athletearena.controller;

import com.website.athletearena.dto.request.LoginRequest;
import com.website.athletearena.dto.request.RegisterRequest;
import com.website.athletearena.dto.response.ApiResponse;
import com.website.athletearena.dto.response.AuthResponse;
import com.website.athletearena.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        ApiResponse response = new ApiResponse(true, "Registration successful", authResponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        ApiResponse response = new ApiResponse(true, "Login successful", authResponse);
        return ResponseEntity.ok(response);
    }
}
