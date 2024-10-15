package com.beotkkot.qtudy.dto.response.mypage;

import com.beotkkot.qtudy.common.ResponseCode;
import com.beotkkot.qtudy.common.ResponseMessage;
import com.beotkkot.qtudy.domain.user.Users;
import com.beotkkot.qtudy.dto.response.ResponseDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetMyPageInfoResponseDto extends ResponseDto {

    private String name;
    private String profileImageUrl;
    private String email;

    @Builder
    private GetMyPageInfoResponseDto(Users user, String email) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.name = user.getName();
        this.email = email;
        this.profileImageUrl = user.getProfileImageUrl();
    }

    public static ResponseEntity<GetMyPageInfoResponseDto> success(Users user, String email) {
        GetMyPageInfoResponseDto result = new GetMyPageInfoResponseDto(user, email);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
