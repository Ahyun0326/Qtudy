package com.beotkkot.qtudy.dto.response.posts;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class PostsResponseDto {
    private Long postId;

    private PostsResponseDto(Long postId) {
        this.postId = postId;
    }

    public static ResponseEntity<PostsResponseDto> success(Long postId) {
        PostsResponseDto result = new PostsResponseDto(postId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
