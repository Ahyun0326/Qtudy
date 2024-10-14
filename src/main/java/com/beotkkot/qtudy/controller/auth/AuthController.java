package com.beotkkot.qtudy.controller.auth;

import com.beotkkot.qtudy.common.exception.error.UserErrorCode;
import com.beotkkot.qtudy.common.exception.exception.UserException;
import com.beotkkot.qtudy.domain.user.Users;
import com.beotkkot.qtudy.dto.object.KakaoUserInfo;
import com.beotkkot.qtudy.dto.response.auth.AuthResponseDto;
import com.beotkkot.qtudy.dto.response.auth.GetAuthResponseDto;
import com.beotkkot.qtudy.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 프론트로부터 인가 code를 받아와 카카오 서버로부터 토큰 얻기
    @GetMapping("")
    public ResponseEntity<? super GetAuthResponseDto> kakaoLogin(@RequestParam("code") String code) {

        // 1. 인가 코드를 통해 카카오 서버로부터 토큰을 얻는다
        String accessToken = authService.getAccessToken(code);
        if (accessToken.isEmpty()) {
            throw new UserException(UserErrorCode.AUTHORIZATION_FAIL);
        }

        // 2. 발급받은 토큰을 이용해 사용자 정보를 조회
        KakaoUserInfo kakaoUserInfo = authService.getKakaoUserInfo(accessToken);
        if (kakaoUserInfo == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        } else {
            // 3. 로그인
            Users user = authService.login(kakaoUserInfo);
            // 4. 유저 정보 리턴
            return GetAuthResponseDto.success(user, accessToken);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<? super AuthResponseDto> logout(@RequestHeader("Authorization") String accessToken) {
        ResponseEntity<? super AuthResponseDto> response = authService.logout(accessToken);
        return response;
    }
}
