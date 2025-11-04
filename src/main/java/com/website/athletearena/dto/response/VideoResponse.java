package com.website.athletearena.dto.response;

import com.website.athletearena.model.enums.VideoCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponse {
    private String id;
    private String title;
    private String description;
    private UserResponse coach;
    private String videoUrl;
    private String thumbnailUrl;
    private VideoCategory category;
    private String duration;
    private int views;
    private LocalDateTime createdAt;
}