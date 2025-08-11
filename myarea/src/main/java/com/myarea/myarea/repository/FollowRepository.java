package com.myarea.myarea.repository;

import com.myarea.myarea.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query(value = "SELECT * FROM follows WHERE user_id = :user_id", nativeQuery = true)
    List<Follow> findByUser_id(@Param("user_id") Long user_id);

    @Query(value = "SELECT * FROM follows WHERE user_id = :user_id AND followed_user_id = :followed_user_id", nativeQuery = true)
    Follow findById(@Param("user_id") Long user_id, @Param("followed_user_id") Long followed_user_id);
}
