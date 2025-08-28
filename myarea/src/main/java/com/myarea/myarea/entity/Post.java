package com.myarea.myarea.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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

//    @Column(name = "image_url", columnDefinition = "TEXT")
//    private String imageUrl;
    @OneToMany(mappedBy = "post",
                cascade = CascadeType.ALL,
                orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @JsonManagedReference
    private List<PostImage> images = new ArrayList<>();

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "loc_id")
    private Location location;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "subloc_id")
//    private SubLocation subLocation;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void patch(Post post) {
        if (post.body != null)
            this.body = post.body;
    }

    public void addImage(PostImage img) {
        images.add(img);
        img.setPost(this);
        img.setDisplayOrder(images.size() - 1);
    }

    public void setImagesReordered(List<PostImage> newImages) {
        images.clear();
        int idx = 0;
        for(PostImage img : newImages) {
            img.setDisplayOrder(idx++);
            addImage(img);
        }
    }

    public List<String> getImageKeyList() {
        List<String> keys = new ArrayList<>();
        for(PostImage img : images) {
            if(img != null && img.getS3_key() != null) {
                keys.add(img.getS3_key());
            }
        }
        return keys;
    }

    public void setImageKeyList(List<String> keysInOrder) {
        if (images == null) images = new ArrayList<>();

        images.clear();
        if(keysInOrder == null) return;

        int idx = 0;
        for(String key: keysInOrder) {
            if(key == null || key.isBlank()) continue;
            PostImage img = PostImage.builder()
                    .s3_key(key)
                    .displayOrder(idx++)
                    .build();
            addImage(img);
        }
    }
}
