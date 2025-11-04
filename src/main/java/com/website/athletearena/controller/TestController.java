package com.website.athletearena.controller;

import com.website.athletearena.dto.response.ApiResponse;
import com.website.athletearena.model.User;
import com.website.athletearena.model.enums.Role;
import com.website.athletearena.repository.UserRepository;
import com.website.athletearena.repository.PostRepository;
import com.website.athletearena.repository.VideoRepository;
import com.website.athletearena.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class TestController {
    
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping("/ping")
    public ResponseEntity<ApiResponse> ping() {
        ApiResponse response = new ApiResponse(true, "Pong! Server is responsive");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/auth-status")
    public ResponseEntity<ApiResponse> authStatus() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean isAuthenticated = SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
            
            Map<String, Object> authData = new HashMap<>();
            authData.put("authenticated", isAuthenticated);
            authData.put("username", username);
            authData.put("message", isAuthenticated ? "User is authenticated" : "User is not authenticated");
            
            ApiResponse response = new ApiResponse(true, "Auth status retrieved", authData);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> authData = new HashMap<>();
            authData.put("authenticated", false);
            authData.put("error", e.getMessage());
            
            ApiResponse response = new ApiResponse(false, "Auth check failed", authData);
            return ResponseEntity.ok(response);
        }
    }
    
    @GetMapping("/database-stats")
    public ResponseEntity<ApiResponse> databaseStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            long userCount = userRepository.count();
            long postCount = postRepository.count();
            long videoCount = videoRepository.count();
            long commentCount = commentRepository.count();
            
            stats.put("users", userCount);
            stats.put("posts", postCount);
            stats.put("videos", videoCount);
            stats.put("comments", commentCount);
            stats.put("totalDocuments", userCount + postCount + videoCount + commentCount);
            stats.put("timestamp", LocalDateTime.now());
            
            ApiResponse response = new ApiResponse(true, "Database stats retrieved", stats);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            stats.put("error", e.getMessage());
            ApiResponse response = new ApiResponse(false, "Failed to retrieve database stats", stats);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/seed-data")
    public ResponseEntity<ApiResponse> seedData() {
        try {
            // Check if data already exists
            if (userRepository.count() > 0) {
                ApiResponse response = new ApiResponse(false, "Database already contains data. Clear it first.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create test users
            User athlete = new User();
            athlete.setName("Test Athlete");
            athlete.setEmail("athlete@test.com");
            athlete.setUsername("testvathlete");
            athlete.setPassword(passwordEncoder.encode("password123"));
            athlete.setRole(Role.ATHLETE);
            athlete.setBio("Test athlete for development");
            athlete.setCreatedAt(LocalDateTime.now());
            athlete.setUpdatedAt(LocalDateTime.now());
            athlete.setActive(true);
            userRepository.save(athlete);
            
            User coach = new User();
            coach.setName("Test Coach");
            coach.setEmail("coach@test.com");
            coach.setUsername("testcoach");
            coach.setPassword(passwordEncoder.encode("password123"));
            coach.setRole(Role.COACH);
            coach.setBio("Test coach for development");
            coach.setCreatedAt(LocalDateTime.now());
            coach.setUpdatedAt(LocalDateTime.now());
            coach.setActive(true);
            userRepository.save(coach);
            
            Map<String, Object> seedData = new HashMap<>();
            seedData.put("usersCreated", 2);
            seedData.put("athleteEmail", "athlete@test.com");
            seedData.put("coachEmail", "coach@test.com");
            seedData.put("defaultPassword", "password123");
            seedData.put("message", "Test data seeded successfully");
            
            ApiResponse response = new ApiResponse(true, "Database seeded with test data", seedData);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Failed to seed data: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @DeleteMapping("/clear-data")
    public ResponseEntity<ApiResponse> clearData() {
        try {
            long usersDeleted = userRepository.count();
            long postsDeleted = postRepository.count();
            long videosDeleted = videoRepository.count();
            long commentsDeleted = commentRepository.count();
            
            userRepository.deleteAll();
            postRepository.deleteAll();
            videoRepository.deleteAll();
            commentRepository.deleteAll();
            
            Map<String, Object> deletionStats = new HashMap<>();
            deletionStats.put("usersDeleted", usersDeleted);
            deletionStats.put("postsDeleted", postsDeleted);
            deletionStats.put("videosDeleted", videosDeleted);
            deletionStats.put("commentsDeleted", commentsDeleted);
            deletionStats.put("totalDeleted", usersDeleted + postsDeleted + videosDeleted + commentsDeleted);
            deletionStats.put("message", "All test data cleared");
            
            ApiResponse response = new ApiResponse(true, "Database cleared successfully", deletionStats);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Failed to clear data: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/environment")
    public ResponseEntity<ApiResponse> environmentInfo() {
        Map<String, Object> envInfo = new HashMap<>();
        
        envInfo.put("javaVersion", System.getProperty("java.version"));
        envInfo.put("osName", System.getProperty("os.name"));
        envInfo.put("osVersion", System.getProperty("os.version"));
        envInfo.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        envInfo.put("maxMemory", Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");
        envInfo.put("freeMemory", Runtime.getRuntime().freeMemory() / (1024 * 1024) + " MB");
        envInfo.put("totalMemory", Runtime.getRuntime().totalMemory() / (1024 * 1024) + " MB");
        
        ApiResponse response = new ApiResponse(true, "Environment info retrieved", envInfo);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/endpoints")
    public ResponseEntity<ApiResponse> listEndpoints() {
        Map<String, Object> endpoints = new HashMap<>();
        
        endpoints.put("health", Map.of(
            "GET /api/health", "Basic health check",
            "GET /api/health/database", "Database connection check",
            "GET /api/health/info", "API information"
        ));
        
        endpoints.put("test", Map.of(
            "GET /api/test/ping", "Simple ping test",
            "GET /api/test/auth-status", "Check authentication status",
            "GET /api/test/database-stats", "Get database statistics",
            "POST /api/test/seed-data", "Seed test data",
            "DELETE /api/test/clear-data", "Clear all test data",
            "GET /api/test/environment", "System environment info",
            "GET /api/test/endpoints", "List all test endpoints"
        ));
        
        endpoints.put("auth", Map.of(
            "POST /api/auth/register", "Register new user",
            "POST /api/auth/login", "Login user"
        ));
        
        endpoints.put("users", Map.of(
            "GET /api/users/me", "Get current user",
            "GET /api/users/{userId}", "Get user by ID",
            "GET /api/users/username/{username}", "Get user by username",
            "GET /api/users/coaches", "Get all coaches",
            "GET /api/users/athletes", "Get all athletes",
            "POST /api/users/follow/{userId}", "Follow user",
            "POST /api/users/unfollow/{userId}", "Unfollow user"
        ));
        
        endpoints.put("posts", Map.of(
            "POST /api/posts", "Create post",
            "GET /api/posts", "Get all posts",
            "GET /api/posts/feed", "Get personalized feed",
            "GET /api/posts/{postId}", "Get post by ID",
            "POST /api/posts/{postId}/like", "Like post",
            "POST /api/posts/{postId}/unlike", "Unlike post",
            "DELETE /api/posts/{postId}", "Delete post"
        ));
        
        endpoints.put("videos", Map.of(
            "POST /api/videos/upload", "Upload video",
            "GET /api/videos", "Get all videos",
            "GET /api/videos/{videoId}", "Get video by ID",
            "GET /api/videos/category/{category}", "Get videos by category",
            "GET /api/videos/search", "Search videos",
            "DELETE /api/videos/{videoId}", "Delete video"
        ));
        
        endpoints.put("comments", Map.of(
            "POST /api/comments/post/{postId}", "Add comment",
            "GET /api/comments/post/{postId}", "Get comments",
            "DELETE /api/comments/{commentId}", "Delete comment"
        ));
        
        ApiResponse response = new ApiResponse(true, "All endpoints listed", endpoints);
        return ResponseEntity.ok(response);
    }
}