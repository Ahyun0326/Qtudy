package com.beotkkot.qtudy.controller.mypage;

import com.beotkkot.qtudy.dto.response.mypage.GetMyInterestResponseDto;
import com.beotkkot.qtudy.dto.response.mypage.GetMyPageAllResponseDto;
import com.beotkkot.qtudy.dto.response.mypage.GetMyPageInfoResponseDto;
import com.beotkkot.qtudy.dto.response.mypage.MyInterestResponseDto;
import com.beotkkot.qtudy.service.auth.AuthService;
import com.beotkkot.qtudy.service.mypage.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final AuthService authService;
    private final MyPageService myPageService;

    // 관심 분야 목록 초기 선택
    @PostMapping("/my/interests")
    public ResponseEntity<MyInterestResponseDto> saveMyInterests(@RequestParam("interests") List<Long> interests, @RequestHeader("Authorization") String token) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return myPageService.saveMyInterests(kakao_uid, interests);
    }

    // 내 관심 분야 목록 조회
    @GetMapping("my/interests")
    public ResponseEntity<GetMyInterestResponseDto> getMyInterests(@RequestHeader("Authorization") String token) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return myPageService.getMyInterests(kakao_uid);
    }

    // 내 관심 분야 목록 수정
    @PatchMapping("my/interests")
    public ResponseEntity<MyInterestResponseDto> patchMyInterests(@RequestParam("interests") List<Long> interests, @RequestHeader("Authorization") String token) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return myPageService.patchMyInterests(kakao_uid, interests);
    }

    // 사용자 프로필 조회
    @GetMapping("/my")
    public ResponseEntity<GetMyPageInfoResponseDto> getMyPageInfo(@RequestHeader("Authorization") String token) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        String email = authService.getKakaoUserInfo(token).getEmail();
        return myPageService.getMyPageInfo(kakao_uid, email);
    }

    // 내가 작성한 게시글 확인
    @GetMapping("my/posts")
    public ResponseEntity<GetMyPageAllResponseDto> getAllPost(@RequestParam("page") int page, @RequestHeader("Authorization") String token) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return myPageService.getAllPost(kakao_uid, page);
    }

    // 내가 스크랩한 글 확인
    @GetMapping("/my/scrap")
    public ResponseEntity<GetMyPageAllResponseDto> getAllScrapPost(@RequestParam("page") int page, @RequestHeader("Authorization") String token) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return myPageService.getAllScrapPost(kakao_uid, page);
    }
}
