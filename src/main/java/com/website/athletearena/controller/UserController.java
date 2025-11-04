package com.website.athletearena.controller;

import com.website.athletearena.dto.response.ApiResponse;
import com.website.athletearena.dto.response.UserResponse;
import com.website.athletearena.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        ApiResponse response = new ApiResponse(true, "User retrieved successfully", user);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable String userId) {
        UserResponse user = userService.getUserById(userId);
        ApiResponse response = new ApiResponse(true, "User retrieved successfully", user);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse> getUserByUsername(@PathVariable String username) {
        UserResponse user = userService.getUserByUsername(username);
        ApiResponse response = new ApiResponse(true, "User retrieved successfully", user);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/coaches")
    public ResponseEntity<ApiResponse> getAllCoaches() {
        List<UserResponse> coaches = userService.getAllCoaches();
        ApiResponse response = new ApiResponse(true, "Coaches retrieved successfully", coaches);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/athletes")
    public ResponseEntity<ApiResponse> getAllAthletes() {
        List<UserResponse> athletes = userService.getAllAthletes();
        ApiResponse response = new ApiResponse(true, "Athletes retrieved successfully", athletes);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/follow/{userId}")
    public ResponseEntity<ApiResponse> followUser(@PathVariable String userId) {
        UserResponse user = userService.followUser(userId);
        ApiResponse response = new ApiResponse(true, "User followed successfully", user);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/unfollow/{userId}")
    public ResponseEntity<ApiResponse> unfollowUser(@PathVariable String userId) {
        UserResponse user = userService.unfollowUser(userId);
        ApiResponse response = new ApiResponse(true, "User unfollowed successfully", user);
        return ResponseEntity.ok(response);
    }
}