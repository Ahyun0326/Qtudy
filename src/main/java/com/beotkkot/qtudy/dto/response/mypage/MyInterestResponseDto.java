package com.beotkkot.qtudy.dto.response.mypage;

import com.beotkkot.qtudy.common.ResponseCode;
import com.beotkkot.qtudy.common.ResponseMessage;
import com.beotkkot.qtudy.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class MyInterestResponseDto extends ResponseDto {

    public MyInterestResponseDto() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);

    }

    public static ResponseEntity<MyInterestResponseDto> success() {
        MyInterestResponseDto result = new MyInterestResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
