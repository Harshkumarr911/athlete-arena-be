package com.website.athletearena.dto.request;

import com.website.athletearena.model.enums.VideoCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VideoUploadRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private VideoCategory category = VideoCategory.TECHNIQUE;
    
    private String duration;
}