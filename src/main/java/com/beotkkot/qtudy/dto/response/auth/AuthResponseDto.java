package com.beotkkot.qtudy.dto.response.auth;

import com.beotkkot.qtudy.common.ResponseCode;
import com.beotkkot.qtudy.common.ResponseMessage;
import com.beotkkot.qtudy.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class AuthResponseDto extends ResponseDto {
    private AuthResponseDto() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<AuthResponseDto> success() {
        AuthResponseDto result = new AuthResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}