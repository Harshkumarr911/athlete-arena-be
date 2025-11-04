package com.website.athletearena.repository;

import com.website.athletearena.model.Video;
import com.website.athletearena.model.enums.VideoCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends MongoRepository<Video, String> {
    Page<Video> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Video> findByCoachIdOrderByCreatedAtDesc(String coachId);
    Page<Video> findByCategoryOrderByCreatedAtDesc(VideoCategory category, Pageable pageable);
    Page<Video> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String title, String description, Pageable pageable);
}