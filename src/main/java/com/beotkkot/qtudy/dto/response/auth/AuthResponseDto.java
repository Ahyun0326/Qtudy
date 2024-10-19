package com.beotkkot.qtudy.dto.response.auth;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class AuthResponseDto {
    public static ResponseEntity<AuthResponseDto> success() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}