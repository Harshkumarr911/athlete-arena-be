package com.website.athletearena.service;

import com.website.athletearena.dto.request.PostRequest;
import com.website.athletearena.dto.response.PostResponse;
import com.website.athletearena.dto.response.UserResponse;
import com.website.athletearena.exception.ResourceNotFoundException;
import com.website.athletearena.model.Post;
import com.website.athletearena.model.User;
import com.website.athletearena.repository.PostRepository;
import com.website.athletearena.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    public PostResponse createPost(PostRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Post post = new Post();
        post.setUserId(user.getId());
        post.setContent(request.getContent());
        post.setVideoUrl(request.getVideoUrl());
        post.setImageUrl(request.getImageUrl());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        
        Post savedPost = postRepository.save(post);
        
        return mapToPostResponse(savedPost, user, false);
    }
    
    public List<PostResponse> getAllPosts(int page, int size) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        return posts.stream()
                .map(post -> {
                    User postUser = userRepository.findById(post.getUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    boolean liked = post.getLikedBy().contains(currentUser.getId());
                    return mapToPostResponse(post, postUser, liked);
                })
                .collect(Collectors.toList());
    }
    
    public List<PostResponse> getFeedPosts(int page, int size) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<String> followingIds = currentUser.getFollowing();
        followingIds.add(currentUser.getId()); // Include own posts
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByUserIdInOrderByCreatedAtDesc(followingIds, pageable);
        
        return posts.stream()
                .map(post -> {
                    User postUser = userRepository.findById(post.getUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    boolean liked = post.getLikedBy().contains(currentUser.getId());
                    return mapToPostResponse(post, postUser, liked);
                })
                .collect(Collectors.toList());
    }
    
    public PostResponse getPostById(String postId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        User postUser = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        boolean liked = post.getLikedBy().contains(currentUser.getId());
        
        return mapToPostResponse(post, postUser, liked);
    }
    
    public PostResponse likePost(String postId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        if (!post.getLikedBy().contains(currentUser.getId())) {
            post.getLikedBy().add(currentUser.getId());
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
        }
        
        User postUser = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return mapToPostResponse(post, postUser, true);
    }
    
    public PostResponse unlikePost(String postId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        if (post.getLikedBy().contains(currentUser.getId())) {
            post.getLikedBy().remove(currentUser.getId());
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
        }
        
        User postUser = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return mapToPostResponse(post, postUser, false);
    }
    
    public void deletePost(String postId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        if (!post.getUserId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Unauthorized to delete this post");
        }
        
        postRepository.delete(post);
    }
    
    private PostResponse mapToPostResponse(Post post, User user, boolean liked) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setUser(mapToUserResponse(user));
        response.setContent(post.getContent());
        response.setVideoUrl(post.getVideoUrl());
        response.setImageUrl(post.getImageUrl());
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setLikedByCurrentUser(liked);
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
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