package com.website.athletearena.controller;

import com.website.athletearena.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class HealthController {
    
    private final MongoTemplate mongoTemplate;
    
    @GetMapping
    public ResponseEntity<ApiResponse> healthCheck() {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("service", "AthleteArena API");
        healthData.put("timestamp", LocalDateTime.now());
        healthData.put("message", "Service is running smoothly");
        
        ApiResponse response = new ApiResponse(true, "Health check successful", healthData);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/database")
    public ResponseEntity<ApiResponse> databaseCheck() {
        Map<String, Object> dbData = new HashMap<>();
        
        try {
            // Try to get database stats
            String dbName = mongoTemplate.getDb().getName();
            dbData.put("status", "CONNECTED");
            dbData.put("database", dbName);
            dbData.put("message", "Database connection is healthy");
            
            ApiResponse response = new ApiResponse(true, "Database check successful", dbData);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            dbData.put("status", "DISCONNECTED");
            dbData.put("error", e.getMessage());
            dbData.put("message", "Database connection failed");
            
            ApiResponse response = new ApiResponse(false, "Database check failed", dbData);
            return ResponseEntity.status(503).body(response);
        }
    }
    
    @GetMapping("/info")
    public ResponseEntity<ApiResponse> apiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "AthleteArena API");
        info.put("version", "1.0.0");
        info.put("description", "Backend API for Athlete Training Platform");
        info.put("timestamp", LocalDateTime.now());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("authentication", "/api/auth");
        endpoints.put("users", "/api/users");
        endpoints.put("posts", "/api/posts");
        endpoints.put("videos", "/api/videos");
        endpoints.put("comments", "/api/comments");
        endpoints.put("health", "/api/health");
        endpoints.put("test", "/api/test");
        
        info.put("endpoints", endpoints);
        
        ApiResponse response = new ApiResponse(true, "API info retrieved", info);
        return ResponseEntity.ok(response);
    }
}