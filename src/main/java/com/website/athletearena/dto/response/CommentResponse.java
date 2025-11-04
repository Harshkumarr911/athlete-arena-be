package com.website.athletearena.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private String id;
    private String postId;
    private UserResponse user;
    private String content;
    private LocalDateTime createdAt;
}