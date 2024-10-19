package com.beotkkot.qtudy.dto.response.mypage;

import com.beotkkot.qtudy.domain.user.Users;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetMyPageInfoResponseDto {

    private String name;
    private String profileImageUrl;
    private String email;

    @Builder
    private GetMyPageInfoResponseDto(Users user, String email) {
        this.name = user.getName();
        this.email = email;
        this.profileImageUrl = user.getProfileImageUrl();
    }

    public static ResponseEntity<GetMyPageInfoResponseDto> success(Users user, String email) {
        GetMyPageInfoResponseDto result = new GetMyPageInfoResponseDto(user, email);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
