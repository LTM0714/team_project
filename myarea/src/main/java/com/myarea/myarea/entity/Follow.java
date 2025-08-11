package com.myarea.myarea.entity;

import com.myarea.myarea.dto.FollowDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "follows")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Getter
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_user_id", nullable = false)
    private User followed_user_id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    public static Follow addFollow(FollowDto dto, User user_id, User followed_user_id) {
        return new Follow(
                dto.getId(),
                user_id,
                followed_user_id,
                dto.getCreated_at()
        );
    }
}
