package com.website.athletearena.service;

import com.website.athletearena.dto.request.CommentRequest;
import com.website.athletearena.dto.response.CommentResponse;
import com.website.athletearena.dto.response.UserResponse;
import com.website.athletearena.exception.ResourceNotFoundException;
import com.website.athletearena.model.Comment;
import com.website.athletearena.model.Post;
import com.website.athletearena.model.User;
import com.website.athletearena.repository.CommentRepository;
import com.website.athletearena.repository.PostRepository;
import com.website.athletearena.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    public CommentResponse addComment(String postId, CommentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(user.getId());


        // Error was here
        // comment.setContent(request.getContent());

        
        comment.setCreatedAt(LocalDateTime.now());
        
        Comment savedComment = commentRepository.save(comment);
        
        // Update post comment count
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        
        return mapToCommentResponse(savedComment, user);
    }
    
    public List<CommentResponse> getCommentsByPostId(String postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
        
        return comments.stream()
                .map(comment -> {
                    User user = userRepository.findById(comment.getUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    return mapToCommentResponse(comment, user);
                })
                .collect(Collectors.toList());
    }
    
    public void deleteComment(String commentId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        if (!comment.getUserId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Unauthorized to delete this comment");
        }
        
        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        postRepository.save(post);
        
        commentRepository.delete(comment);
    }
    
    private CommentResponse mapToCommentResponse(Comment comment, User user) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPostId());
        response.setUser(mapToUserResponse(user));
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
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