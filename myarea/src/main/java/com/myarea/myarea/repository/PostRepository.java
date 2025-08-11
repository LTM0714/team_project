package com.myarea.myarea.repository;

import com.myarea.myarea.entity.Follow;
import com.myarea.myarea.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Override
    ArrayList<Post> findAll();
}
