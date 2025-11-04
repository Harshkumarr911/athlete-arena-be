package com.website.athletearena.controller;

import com.website.athletearena.dto.request.CommentRequest;
import com.website.athletearena.dto.response.ApiResponse;
import com.website.athletearena.dto.response.CommentResponse;
import com.website.athletearena.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class CommentController {
    
    private final CommentService commentService;
    
    @PostMapping("/post/{postId}")
    public ResponseEntity<ApiResponse> addComment(
            @PathVariable String postId,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse comment = commentService.addComment(postId, request);
        ApiResponse response = new ApiResponse(true, "Comment added successfully", comment);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse> getCommentsByPostId(@PathVariable String postId) {
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        ApiResponse response = new ApiResponse(true, "Comments retrieved successfully", comments);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        ApiResponse response = new ApiResponse(true, "Comment deleted successfully");
        return ResponseEntity.ok(response);
    }
}