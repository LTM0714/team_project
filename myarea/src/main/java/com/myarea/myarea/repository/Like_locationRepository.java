package com.myarea.myarea.repository;

import com.myarea.myarea.entity.Like_location;
import com.myarea.myarea.entity.Like_post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Like_locationRepository extends JpaRepository<Like_location, Long> {
    @Query(value = "SELECT * FROM like_location WHERE user_id = :user_id", nativeQuery = true)
    List<Like_location> findByUser_id(@Param("user_id") Long user_id);

    @Query(value = "SELECT * FROM like_location WHERE user_id = :user_id AND subloc_id = :sublocation_id", nativeQuery = true)
    Like_location findById(@Param("user_id") Long user_id, @Param("sublocation_id") Long sublocation_id);

}
