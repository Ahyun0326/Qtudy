package com.beotkkot.qtudy.common.exception.response;

import com.beotkkot.qtudy.common.exception.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class ErrorResponse {

    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<ValidationErrorResponse> errors;

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(message)
                .build();
    }

    public static ErrorResponse of(List<ValidationErrorResponse> validationErrorResponses, ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .errors(validationErrorResponses)
                .message(errorCode.getMessage())
                .build();
    }
}
