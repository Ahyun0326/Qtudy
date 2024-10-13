package com.beotkkot.qtudy.common.exception.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;

@Getter
@Builder
@RequiredArgsConstructor
public class ValidationErrorResponse {

    private final String field;
    private final String message;

    public static ValidationErrorResponse of(FieldError fieldError) {
        return ValidationErrorResponse.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
    }
}
