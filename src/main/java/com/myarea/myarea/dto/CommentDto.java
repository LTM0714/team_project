package com.myarea.myarea.dto;

import com.myarea.myarea.entity.Comment;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentDto {
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;

    public static CommentDto createCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getPost().getPostId(),
                comment.getUser().getId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
