package com.beotkkot.qtudy.dto.response.mypage;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetMyInterestResponseDto {

    private List<Long> interests;

    public GetMyInterestResponseDto(List<Long> interests) {
        this.interests = interests;
    }

    public static ResponseEntity<GetMyInterestResponseDto> success(List<Long> interestIds) {
        GetMyInterestResponseDto result = new GetMyInterestResponseDto(interestIds);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
