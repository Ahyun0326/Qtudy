package com.beotkkot.qtudy.dto.response.posts;

import com.beotkkot.qtudy.common.ResponseCode;
import com.beotkkot.qtudy.common.ResponseMessage;
import com.beotkkot.qtudy.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class PutScrapResponseDto extends ResponseDto {

    private PutScrapResponseDto() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<PutScrapResponseDto> success() {
        PutScrapResponseDto result = new PutScrapResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
