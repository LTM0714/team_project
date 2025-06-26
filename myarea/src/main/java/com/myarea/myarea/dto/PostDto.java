package com.myarea.myarea.dto;

import com.myarea.myarea.entity.Location;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.User;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@ToString
@Setter
public class PostDto {
    private Long postId;
    private Long userId;
    private String imageUrl;
    private String body;
    private Long locId;
    // private Long subLocId;
    private LocalDateTime createdAt;

    // 전송 받은 이미지와 내용을 필드에 저장하는 생성자 추가 --> @AllArgsConstructor

    // 데이터를 잘 받았는지 확인할 toString() 메서드 추가 --> @ToString

    //폼 데이터를 받은 DTO객체를 엔티티로 반환
    public Post toEntity() {
        User user = new User();
        user.setId(userId);

        Location location = new Location();
        location.setLocId(locId);

        return new Post(postId, user, imageUrl, body, location, createdAt);
    }
}
