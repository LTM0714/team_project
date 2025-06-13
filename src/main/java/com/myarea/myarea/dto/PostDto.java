package com.myarea.myarea.dto;

import com.myarea.myarea.entity.Location;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDto {
    private Long postId;
    private Long userId;
    private String imageUrl;
    private String body;
    private Long locId;
    // private Long subLocId;
    private LocalDateTime createdAt;

    public Post toEntity() {
        User user = new User();
        user.setId(userId); // User 객체에 userId 설정

        Location location = new Location();
        location.setLocId(locId);

//        SubLocation subLocation = new SubLocation();
//        subLocation.setsubLocId(subLocId);

        return new Post(postId, user, imageUrl, body, location, createdAt);
    }
}
