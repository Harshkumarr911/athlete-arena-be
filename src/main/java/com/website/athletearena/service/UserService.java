package com.website.athletearena.service;

import com.website.athletearena.dto.response.UserResponse;
import com.website.athletearena.exception.ResourceNotFoundException;
import com.website.athletearena.model.User;
import com.website.athletearena.model.enums.Role;
import com.website.athletearena.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user);
    }
    
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user);
    }
    
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user);
    }
    
    public List<UserResponse> getAllCoaches() {
        return userRepository.findByRole(Role.COACH)
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    public List<UserResponse> getAllAthletes() {
        return userRepository.findByRole(Role.ATHLETE)
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    public UserResponse followUser(String userId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!currentUser.getFollowing().contains(userId)) {
            currentUser.getFollowing().add(userId);
            currentUser.setFollowingCount(currentUser.getFollowingCount() + 1);
            
            targetUser.getFollowers().add(currentUser.getId());
            targetUser.setFollowerCount(targetUser.getFollowerCount() + 1);
            
            userRepository.save(currentUser);
            userRepository.save(targetUser);
        }
        
        return mapToUserResponse(targetUser);
    }
    
    public UserResponse unfollowUser(String userId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (currentUser.getFollowing().contains(userId)) {
            currentUser.getFollowing().remove(userId);
            currentUser.setFollowingCount(currentUser.getFollowingCount() - 1);
            
            targetUser.getFollowers().remove(currentUser.getId());
            targetUser.setFollowerCount(targetUser.getFollowerCount() - 1);
            
            userRepository.save(currentUser);
            userRepository.save(targetUser);
        }
        
        return mapToUserResponse(targetUser);
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