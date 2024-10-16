package com.beotkkot.qtudy.dto.response.mypage;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class MyInterestResponseDto {

    public static ResponseEntity<MyInterestResponseDto> success() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
