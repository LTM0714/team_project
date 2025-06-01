package com.myarea.myarea.repository;

import com.myarea.myarea.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 사용자별 게시물 조회
    Page<Post> findByUserId(Long userId, Pageable pageable);
    
    // 위치별 게시물 조회(일단 locationId로 조회해났는데 고쳐야 함)
    @Query("SELECT p FROM Post p WHERE p.location.id = :locationId")
    Page<Post> findByLocationId(@Param("locationId") Long locationId, Pageable pageable);

    // 장소 이름으로 게시물 검색 기능
    @Query("SELECT p FROM Post p WHERE p.location.locationName LIKE %:keyword%")
    Page<Post> findByLocationNameContaining(@Param("keyword") String keyword, Pageable pageable);
} 