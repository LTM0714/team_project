package com.myarea.myarea.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {
    private Long postId;
    private Long userId;
    private String content;
}
