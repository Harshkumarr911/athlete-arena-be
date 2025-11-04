package com.website.athletearena.controller;

import com.website.athletearena.dto.request.VideoUploadRequest;
import com.website.athletearena.dto.response.ApiResponse;
import com.website.athletearena.dto.response.VideoResponse;
import com.website.athletearena.model.enums.VideoCategory;
import com.website.athletearena.service.FileStorageService;
import com.website.athletearena.service.VideoService;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.nio.file.Paths;
import java.util.List;
import java.nio.file.Path;
// import java.nio.file.Paths;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class VideoController {
    
    private final VideoService videoService;
    private final FileStorageService fileStorageService;
    
    // @PostMapping("/upload")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadVideo(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", defaultValue = "TECHNIQUE") VideoCategory category,
            @RequestParam(value = "duration", required = false) String duration,
            @RequestParam("video") MultipartFile video,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail) {
        
        // Store video file
        String videoUrl = fileStorageService.storeFile(video, "videos");
        
        // Store thumbnail if provided
        String thumbnailUrl = null;
        if (thumbnail != null && !thumbnail.isEmpty()) {
            thumbnailUrl = fileStorageService.storeFile(thumbnail, "thumbnails");
        }
        
        VideoUploadRequest request = new VideoUploadRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setCategory(category);
        request.setDuration(duration);
        
        VideoResponse videoResponse = videoService.uploadVideo(request, videoUrl, thumbnailUrl);
        ApiResponse response = new ApiResponse(true, "Video uploaded successfully", videoResponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<VideoResponse> videos = videoService.getAllVideos(page, size);
        ApiResponse response = new ApiResponse(true, "Videos retrieved successfully", videos);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse> getVideosByCategory(
            @PathVariable VideoCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<VideoResponse> videos = videoService.getVideosByCategory(category, page, size);
        ApiResponse response = new ApiResponse(true, "Videos retrieved successfully", videos);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{videoId}")
    public ResponseEntity<ApiResponse> getVideoById(@PathVariable String videoId) {
        VideoResponse video = videoService.getVideoById(videoId);
        ApiResponse response = new ApiResponse(true, "Video retrieved successfully", video);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchVideos(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<VideoResponse> videos = videoService.searchVideos(query, page, size);
        ApiResponse response = new ApiResponse(true, "Videos retrieved successfully", videos);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{videoId}")
    public ResponseEntity<ApiResponse> deleteVideo(@PathVariable String videoId) {
        videoService.deleteVideo(videoId);
        ApiResponse response = new ApiResponse(true, "Video deleted successfully");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/stream/{filename}")
    public ResponseEntity<Resource> streamVideo(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/videos").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                String contentType = "video/mp4";
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}