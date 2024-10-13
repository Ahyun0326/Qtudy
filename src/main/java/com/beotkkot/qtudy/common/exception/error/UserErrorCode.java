package com.beotkkot.qtudy.common.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    NOT_EXISTED_USER(HttpStatus.BAD_REQUEST, "This user does not exist"),
    AUTHORIZATION_FAIL(HttpStatus.UNAUTHORIZED, "Authorization failed"), // 유효한 인증 정보 부족
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
