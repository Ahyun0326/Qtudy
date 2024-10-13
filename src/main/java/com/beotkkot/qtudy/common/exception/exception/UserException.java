package com.beotkkot.qtudy.common.exception.exception;

import com.beotkkot.qtudy.common.exception.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {
    private final ErrorCode errorCode;

}
