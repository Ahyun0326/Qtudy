package com.beotkkot.qtudy.dto.response.comments;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class CommentsResponseDto {

    private String name;
    private String profileImageUrl;

    public CommentsResponseDto(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public static ResponseEntity<CommentsResponseDto> success(String name, String profileImageUrl) {
        CommentsResponseDto result = new CommentsResponseDto(name, profileImageUrl);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
