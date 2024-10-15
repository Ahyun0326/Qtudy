package com.beotkkot.qtudy.dto.response.comments;

import com.beotkkot.qtudy.common.ResponseCode;
import com.beotkkot.qtudy.common.ResponseMessage;
import com.beotkkot.qtudy.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class DeleteCommentsResponseDto extends ResponseDto{
    public DeleteCommentsResponseDto() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<DeleteCommentsResponseDto> success() {
        DeleteCommentsResponseDto result = new DeleteCommentsResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
