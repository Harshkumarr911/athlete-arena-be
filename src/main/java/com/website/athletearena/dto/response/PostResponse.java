package com.website.athletearena.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private String id;
    private UserResponse user;
    private String content;
    private String videoUrl;
    private String imageUrl;
    private int likeCount;
    private int commentCount;
    private boolean likedByCurrentUser;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}