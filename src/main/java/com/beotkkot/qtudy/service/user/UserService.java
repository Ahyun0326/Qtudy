package com.beotkkot.qtudy.service.user;

import com.beotkkot.qtudy.domain.user.Users;
import com.beotkkot.qtudy.dto.object.KakaoUserInfo;
import com.beotkkot.qtudy.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // 사용자 저장
    @Transactional
    public Long saveUser(KakaoUserInfo kakaoUserInfo) {

        String name = kakaoUserInfo.getName();

        Users user = Users.builder()
                .name(name)
                .kakaoId(kakaoUserInfo.getId())
                .profileImageUrl(kakaoUserInfo.getProfileImageUrl())
                .first(true)
                .build();

        userRepository.save(user);

        return user.getUserId();
    }
}
