package com.website.athletearena.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostRequest {
    @NotBlank(message = "Content is required")
    private String content;
    
    private String videoUrl;
    private String imageUrl;
}