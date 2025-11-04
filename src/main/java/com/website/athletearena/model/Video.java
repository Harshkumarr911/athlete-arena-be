package com.website.athletearena.model;

import com.website.athletearena.model.enums.VideoCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "videos")
public class Video {
    @Id
    private String id;
    
    private String title;
    
    private String description;
    
    private String coachId;
    
    private String videoUrl;
    
    private String thumbnailUrl;
    
    private VideoCategory category;
    
    private String duration;
    
    private int views = 0;
    
    private LocalDateTime createdAt = LocalDateTime.now();
}