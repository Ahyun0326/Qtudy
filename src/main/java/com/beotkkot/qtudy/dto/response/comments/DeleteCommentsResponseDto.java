package com.beotkkot.qtudy.dto.response.comments;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class DeleteCommentsResponseDto {
    public static ResponseEntity<DeleteCommentsResponseDto> success() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
