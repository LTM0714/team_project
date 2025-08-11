package com.myarea.myarea.entity;

import com.myarea.myarea.dto.Like_postDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "like_post")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Getter
public class Like_post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post_id;

    @CreationTimestamp
    @Column(name = "liked_at", updatable = false)
    private LocalDateTime liked_at;

    public static Like_post addLike_post(Like_postDto dto, User user_id, Post post_id) {
        return new Like_post(
                dto.getId(),
                user_id,
                post_id,
                dto.getLiked_at()
        );
    }
}
