package com.website.athletearena.service;

import com.website.athletearena.dto.request.VideoUploadRequest;
import com.website.athletearena.dto.response.UserResponse;
import com.website.athletearena.dto.response.VideoResponse;
import com.website.athletearena.exception.ResourceNotFoundException;
import com.website.athletearena.model.User;
import com.website.athletearena.model.Video;
import com.website.athletearena.model.enums.VideoCategory;
import com.website.athletearena.repository.UserRepository;
import com.website.athletearena.repository.VideoRepository;
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
public class VideoService {
    
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    
    public VideoResponse uploadVideo(VideoUploadRequest request, String videoUrl, String thumbnailUrl) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Video video = new Video();
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setCoachId(coach.getId());
        video.setVideoUrl(videoUrl);
        video.setThumbnailUrl(thumbnailUrl);
        video.setCategory(request.getCategory());
        video.setDuration(request.getDuration());
        video.setCreatedAt(LocalDateTime.now());
        
        Video savedVideo = videoRepository.save(video);
        
        return mapToVideoResponse(savedVideo, coach);
    }
    
    public List<VideoResponse> getAllVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> videos = videoRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        return videos.stream()
                .map(video -> {
                    User coach = userRepository.findById(video.getCoachId())
                            .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));
                    return mapToVideoResponse(video, coach);
                })
                .collect(Collectors.toList());
    }
    
    public List<VideoResponse> getVideosByCategory(VideoCategory category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> videos = videoRepository.findByCategoryOrderByCreatedAtDesc(category, pageable);
        
        return videos.stream()
                .map(video -> {
                    User coach = userRepository.findById(video.getCoachId())
                            .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));
                    return mapToVideoResponse(video, coach);
                })
                .collect(Collectors.toList());
    }
    
    public VideoResponse getVideoById(String videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found"));
        
        // Increment view count
        video.setViews(video.getViews() + 1);
        videoRepository.save(video);
        
        User coach = userRepository.findById(video.getCoachId())
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));
        
        return mapToVideoResponse(video, coach);
    }
    
    public List<VideoResponse> searchVideos(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> videos = videoRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                query, query, pageable);
        
        return videos.stream()
                .map(video -> {
                    User coach = userRepository.findById(video.getCoachId())
                            .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));
                    return mapToVideoResponse(video, coach);
                })
                .collect(Collectors.toList());
    }
    
    public void deleteVideo(String videoId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found"));
        
        if (!video.getCoachId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Unauthorized to delete this video");
        }
        
        videoRepository.delete(video);
    }
    
    private VideoResponse mapToVideoResponse(Video video, User coach) {
        VideoResponse response = new VideoResponse();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setDescription(video.getDescription());
        response.setCoach(mapToUserResponse(coach));
        response.setVideoUrl(video.getVideoUrl());
        response.setThumbnailUrl(video.getThumbnailUrl());
        response.setCategory(video.getCategory());
        response.setDuration(video.getDuration());
        response.setViews(video.getViews());
        response.setCreatedAt(video.getCreatedAt());
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
