package com.myarea.myarea.repository;

import com.myarea.myarea.entity.Like_post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Like_postRepository extends JpaRepository<Like_post, Long> {
    @Query(value = "SELECT * FROM like_post WHERE user_id = :user_id", nativeQuery = true)
    List<Like_post> findByUser_id(@Param("user_id") Long user_id);

    @Query(value = "SELECT * FROM like_post WHERE user_id = :user_id AND post_id = :post_id", nativeQuery = true)
    Like_post findById(@Param("user_id") Long user_id, @Param("post_id") Long post_id);
}
