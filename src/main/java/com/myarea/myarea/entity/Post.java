package com.myarea.myarea.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Table(name = "post")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loc_id")
    private Location location;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "subloc_id")
//    private SubLocation subLocation;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public void patch(Post post) {
        if (post.imageUrl != null)
            this.imageUrl = post.imageUrl;
        if (post.body != null)
            this.body = post.body;
    }
}
