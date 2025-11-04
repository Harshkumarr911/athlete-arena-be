package com.website.athletearena.service;

import com.website.athletearena.dto.request.LoginRequest;
import com.website.athletearena.dto.request.RegisterRequest;
import com.website.athletearena.dto.response.AuthResponse;
import com.website.athletearena.dto.response.UserResponse;
import com.website.athletearena.exception.BadRequestException;
import com.website.athletearena.model.User;
import com.website.athletearena.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        
        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setBio(request.getBio());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(true);
        
        User savedUser = userRepository.save(user);
        
        // Generate JWT token
        String token = jwtService.generateToken(savedUser.getEmail());
        
        // Convert to response
        UserResponse userResponse = mapToUserResponse(savedUser);
        
        return new AuthResponse(token, userResponse);
    }
    
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmailOrUsername(),
                request.getPassword()
            )
        );
        
        // Find user
        User user = userRepository.findByEmail(request.getEmailOrUsername())
                .or(() -> userRepository.findByUsername(request.getEmailOrUsername()))
                .orElseThrow(() -> new BadRequestException("User not found"));
        
        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail());
        
        // Convert to response
        UserResponse userResponse = mapToUserResponse(user);
        
        return new AuthResponse(token, userResponse);
    }
    
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        response.setBio(user.getBio());
        response.setFollowerCount(user.getFollowerCount());
        response.setFollowingCount(user.getFollowingCount());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
