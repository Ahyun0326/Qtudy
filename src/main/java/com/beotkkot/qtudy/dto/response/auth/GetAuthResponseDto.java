package com.beotkkot.qtudy.dto.response.auth;

import com.beotkkot.qtudy.common.ResponseCode;
import com.beotkkot.qtudy.common.ResponseMessage;
import com.beotkkot.qtudy.domain.user.Users;
import com.beotkkot.qtudy.dto.response.ResponseDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetAuthResponseDto extends ResponseDto {

    private Long kakaoId;
    private Long id;
    private String accessToken;
    private String name;
    private String profileImageUrl;
    private boolean first;

    @Builder
    private GetAuthResponseDto(Users user, String accessToken) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);

        this.kakaoId = user.getKakaoId();
        this.id = user.getUserId();
        this.accessToken = accessToken;
        this.name = user.getName();
        this.profileImageUrl = user.getProfileImageUrl();
        this.first = user.isFirst();
    }

    public static ResponseEntity<GetAuthResponseDto> success(Users user, String accessToken) {
        GetAuthResponseDto result = new GetAuthResponseDto(user, accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
