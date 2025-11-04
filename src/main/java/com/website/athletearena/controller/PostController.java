package com.website.athletearena.controller;

import com.website.athletearena.dto.request.PostRequest;
import com.website.athletearena.dto.response.ApiResponse;
import com.website.athletearena.dto.response.PostResponse;
import com.website.athletearena.service.FileStorageService;
import com.website.athletearena.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;


import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class PostController {
    
    private final PostService postService;
    private final FileStorageService fileStorageService;
    
    @PostMapping
    public ResponseEntity<ApiResponse> createPost(@Valid @RequestBody PostRequest request) {
        PostResponse post = postService.createPost(request);
        ApiResponse response = new ApiResponse(true, "Post created successfully", post);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping(value = "/with-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createPostWithMedia(
            @RequestParam("content") String content,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        System.out.println("Received request -> content=" + content);
        System.out.println("Image file: " + (image != null ? image.getOriginalFilename() : "null"));
        System.out.println("Video file: " + (video != null ? video.getOriginalFilename() : "null"));
        
        // boolean hasImage=false;       
        PostRequest request = new PostRequest();
        request.setContent(content);
        
        if (video != null && !video.isEmpty()) {
            String videoUrl = fileStorageService.storeFile(video, "videos");
            request.setVideoUrl(videoUrl);
        }
        
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(image, "images");
            request.setImageUrl(imageUrl);

        }
        // if(hasImage==false || image!=null){
        //     String mess="Image not uploaded";
        // }
        
        PostResponse post = postService.createPost(request);
        ApiResponse response = new ApiResponse(true, "Post created successfully", post);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PostResponse> posts = postService.getAllPosts(page, size);
        ApiResponse response = new ApiResponse(true, "Posts retrieved successfully", posts);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/feed")
    public ResponseEntity<ApiResponse> getFeedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PostResponse> posts = postService.getFeedPosts(page, size);
        ApiResponse response = new ApiResponse(true, "Feed retrieved successfully", posts);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse> getPostById(@PathVariable String postId) {
        PostResponse post = postService.getPostById(postId);
        ApiResponse response = new ApiResponse(true, "Post retrieved successfully", post);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse> likePost(@PathVariable String postId) {
        PostResponse post = postService.likePost(postId);
        ApiResponse response = new ApiResponse(true, "Post liked successfully", post);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{postId}/unlike")
    public ResponseEntity<ApiResponse> unlikePost(@PathVariable String postId) {
        PostResponse post = postService.unlikePost(postId);
        ApiResponse response = new ApiResponse(true, "Post unliked successfully", post);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
        ApiResponse response = new ApiResponse(true, "Post deleted successfully");
        return ResponseEntity.ok(response);
    }
}