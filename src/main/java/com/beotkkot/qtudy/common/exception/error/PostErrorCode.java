package com.beotkkot.qtudy.common.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {

    NOT_EXISTED_POST(HttpStatus.BAD_REQUEST, "This post does not exist");

    private final HttpStatus httpStatus;
    private final String message;

}
