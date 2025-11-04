package com.website.athletearena.repository;

import com.website.athletearena.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Post> findByUserIdOrderByCreatedAtDesc(String userId);
    Page<Post> findByUserIdInOrderByCreatedAtDesc(List<String> userIds, Pageable pageable);
}