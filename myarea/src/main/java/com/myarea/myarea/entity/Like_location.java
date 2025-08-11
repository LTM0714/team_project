package com.myarea.myarea.entity;

import com.myarea.myarea.dto.Like_locationDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "like_location")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Getter
public class Like_location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subloc_id", nullable = false)
    private SubLocation subloc_id;

    @CreationTimestamp
    @Column(name = "liked_at", updatable = false)
    private LocalDateTime liked_at;

    public static Like_location addLike_location(Like_locationDto dto, User user_id, SubLocation subloc_id) {
        return new Like_location(
                dto.getId(),
                user_id,
                subloc_id,
                dto.getLiked_at()
        );
    }
}
