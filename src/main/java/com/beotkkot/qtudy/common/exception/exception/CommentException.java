package com.beotkkot.qtudy.common.exception.exception;

import com.beotkkot.qtudy.common.exception.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentException extends RuntimeException {
    private final ErrorCode errorCode;
}
