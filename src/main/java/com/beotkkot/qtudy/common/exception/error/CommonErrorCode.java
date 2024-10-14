package com.beotkkot.qtudy.common.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode{

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
    DUPLICATED_VALUE(HttpStatus.BAD_REQUEST, "Value already exists"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "Do not have permission"),  // 로그인은 했지만 접근 권한 X
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
