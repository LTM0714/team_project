package com.myarea.myarea.repository;

import com.myarea.myarea.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
