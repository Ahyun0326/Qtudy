package com.beotkkot.qtudy.dto.response.posts;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class PutScrapResponseDto {


    public static ResponseEntity<PutScrapResponseDto> success() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
