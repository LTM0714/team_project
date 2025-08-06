package com.myarea.myarea.entity;

import com.myarea.myarea.dto.CommentDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.myarea.myarea.entity.UserRole.ADMIN;

@Entity
@Table(name = "comment")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id")
    private User user;

    @Column(length = 500)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public static Comment createComment(CommentDto dto, Post post, User user) {
        //예외 발생
        if(dto.getCommentId()!=null)
            throw new IllegalArgumentException("댓글 생성 실패! 댓글의 id가 없어야 합니다.");
        //엔티티 생성 및 반환
        return new Comment(
                dto.getCommentId(),
                post,
                user,
                dto.getContent(),
                dto.getCreatedAt()
        );
    }

    public void patch(CommentDto dto, User user, Comment target) {
        //예외 발생
        if(user.getId()==null)
            throw new IllegalArgumentException("댓글 수정 실패! 로그인해야 합니다.");
        if(target.getUser().getId()!=user.getId() && user.getRole()!= ADMIN)
            throw new IllegalArgumentException("댓글 수정 실패! 해당 댓글을 쓴 유저가 아니거나 admin이 아닙니다.");
        //객체 갱신
        if(dto.getContent()!=null)
            this.content = dto.getContent();
    }
}
