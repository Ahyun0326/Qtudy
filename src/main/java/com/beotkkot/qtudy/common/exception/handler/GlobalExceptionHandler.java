package com.beotkkot.qtudy.common.exception.handler;

import com.beotkkot.qtudy.common.exception.exception.CommentException;
import com.beotkkot.qtudy.common.exception.exception.CommonException;
import com.beotkkot.qtudy.common.exception.exception.PostException;
import com.beotkkot.qtudy.common.exception.error.CommonErrorCode;
import com.beotkkot.qtudy.common.exception.error.ErrorCode;
import com.beotkkot.qtudy.common.exception.exception.UserException;
import com.beotkkot.qtudy.common.exception.response.ErrorResponse;
import com.beotkkot.qtudy.common.exception.response.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler { // 스프링 예외를 미리 처리해둔 ResponseEntityExceptionHandler 상속

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<Object> handleCommonException(CommonException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Object> handleUserException(UserException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<Object> handleUserException(PostException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<Object> handleCommentException(CommentException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) { //  잘못된 파라미터에 의한 예외
        log.warn("handleIllegalArgument", e);
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid( // @Valid에 의한 유효성 검증 실패 예외
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        log.warn("handleIllegalArgument", e);
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(e, errorCode);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllException(Exception e) {
        log.warn("handleAllException", e);
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(errorCode);
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, message));
    }

    private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
        List<ValidationErrorResponse> validationErrorResponses = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ValidationErrorResponse::of)
                .collect(toList());

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(validationErrorResponses, errorCode));
    }
}
