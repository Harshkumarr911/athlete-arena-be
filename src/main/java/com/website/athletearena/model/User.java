package com.website.athletearena.model;

import com.website.athletearena.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    private String name;
    
    @Indexed(unique = true)
    private String email;
    
    @Indexed(unique = true)
    private String username;
    
    private String password;
    
    private Role role;
    
    private String avatar;
    
    private String bio;
    
    private List<String> followers = new ArrayList<>();
    
    private List<String> following = new ArrayList<>();
    
    private int followerCount = 0;
    
    private int followingCount = 0;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    private boolean active = true;
}