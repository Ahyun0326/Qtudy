package com.beotkkot.qtudy.common.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    NOT_EXISTED_COMMENT(HttpStatus.BAD_REQUEST, "This comment does not exist.");

    private final HttpStatus httpStatus;
    private final String message;

}
