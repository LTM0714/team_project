package com.myarea.myarea.repository;

import com.myarea.myarea.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //특정 게시글의 모든 댓글 조회
    @Query(value = "SELECT * FROM comment WHERE fk_post_id = :postId", nativeQuery = true)
    List<Comment> findByPostId(@Param("postId") Long postId);
}