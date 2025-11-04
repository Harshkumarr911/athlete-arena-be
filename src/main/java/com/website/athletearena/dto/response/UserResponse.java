package com.website.athletearena.dto.response;

import com.website.athletearena.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String username;
    private Role role;
    private String avatar;
    private String bio;
    private int followerCount;
    private int followingCount;
    private LocalDateTime createdAt;
}