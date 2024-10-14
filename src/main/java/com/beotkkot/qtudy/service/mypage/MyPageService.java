package com.beotkkot.qtudy.service.mypage;

import com.beotkkot.qtudy.common.exception.error.CommonErrorCode;
import com.beotkkot.qtudy.common.exception.error.UserErrorCode;
import com.beotkkot.qtudy.common.exception.exception.CommonException;
import com.beotkkot.qtudy.common.exception.exception.UserException;
import com.beotkkot.qtudy.domain.category.Category;
import com.beotkkot.qtudy.domain.interests.Interests;
import com.beotkkot.qtudy.domain.posts.Posts;
import com.beotkkot.qtudy.domain.user.Users;
import com.beotkkot.qtudy.dto.object.PostListItem;
import com.beotkkot.qtudy.dto.response.mypage.GetMyInterestResponseDto;
import com.beotkkot.qtudy.dto.response.mypage.GetMyPageAllResponseDto;
import com.beotkkot.qtudy.dto.response.mypage.GetMyPageInfoResponseDto;
import com.beotkkot.qtudy.dto.response.mypage.MyInterestResponseDto;
import com.beotkkot.qtudy.repository.category.CategoryRepository;
import com.beotkkot.qtudy.repository.interests.InterestsRepository;
import com.beotkkot.qtudy.repository.posts.PostsRepository;
import com.beotkkot.qtudy.repository.scrap.ScrapRepository;
import com.beotkkot.qtudy.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyPageService {

    private final PostsRepository postRepo;
    private final UserRepository userRepo;
    private final ScrapRepository scrapRepo;
    private final CategoryRepository categoryRepo;
    private final InterestsRepository interestsRepo;

    // 관심사 저장
    @Transactional
    public ResponseEntity<? super MyInterestResponseDto> saveMyInterests(Long kakao_uid, List<Long> interests) {

        Users user = userRepo.findByKakaoId(kakao_uid);
        if (user == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }

        List<Interests> findInterests = interestsRepo.findByUser_UserId(user.getUserId());
        if ((interests.size() > 3) || (!findInterests.isEmpty())) {
            throw new CommonException(CommonErrorCode.INVALID_PARAMETER);
        }

        for (Long catetoryId : interests) {
            // 사용자의 관심사 3개를 interestsRepo에 저장한다.
            // 1. CategoryRepo에서 사용자가 선택한 interests에 해당되는 관심사 조회
            Category category = categoryRepo.findById(catetoryId)
                    .orElseThrow(() -> new CommonException(CommonErrorCode.RESOURCE_NOT_FOUND));
            Interests interest = new Interests(user, category);
            interestsRepo.save(interest);
        }
        return MyInterestResponseDto.success();
    }

    // 관심 분야 목록 조회
    @Transactional(readOnly = true)
    public ResponseEntity<? super GetMyInterestResponseDto> getMyInterests(Long kakao_uid) {

        List<Long> interestIds = new ArrayList<>();
        Users user = userRepo.findByKakaoId(kakao_uid);
        if (user == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }

        List<Interests> interests = interestsRepo.findByUser_UserId(user.getUserId());
        for (Interests interest : interests) {
            interestIds.add(interest.getCategory().getCategoryId());
        }
        return GetMyInterestResponseDto.success(interestIds);
    }

    // 마이페이지에서 자신의 정보 조회 (관심사, 사용자 이름, 이메일, 사용자 프로필이미지)
    @Transactional(readOnly = true)
    public ResponseEntity<? super GetMyPageInfoResponseDto> getMyPageInfo(Long kakao_uid, String email) {
        Users user = userRepo.findByKakaoId(kakao_uid);
        if (user == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }
        return GetMyPageInfoResponseDto.success(user, email);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<? super GetMyPageAllResponseDto> getAllPost(Long kakao_uid, int page) {
        List<PostListItem> postListItems = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page, 6, Sort.by("createdAt").descending());

        Page<Posts> posts = postRepo.findAllByUser_KakaoId(kakao_uid, pageRequest);
        for (Posts post : posts.getContent())
            postListItems.add(PostListItem.of(post));

        return GetMyPageAllResponseDto.success(postListItems, page, posts.getTotalPages());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<? super GetMyPageAllResponseDto> getAllScrapPost(Long kakao_uid, int page) {

        List<PostListItem> postListItems = new ArrayList<>();
        Users user = userRepo.findByKakaoId(kakao_uid);
        if (user == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }

        PageRequest pageRequest = PageRequest.of(page, 6);

        Page<Posts> posts = scrapRepo.findScrapPostsByUserId(user.getUserId(), pageRequest);
        for (Posts post : posts.getContent())
            postListItems.add(PostListItem.of(post));

        return GetMyPageAllResponseDto.success(postListItems, page, posts.getTotalPages());
    }

    @Transactional
    public ResponseEntity<? super MyInterestResponseDto> patchMyInterests(Long kakao_uid, List<Long> interests) {
        Users user = userRepo.findByKakaoId(kakao_uid);
        if (user == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }
        if (interests.size() > 3) {
            throw new CommonException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 기존의 관심사를 모두 삭제
        interestsRepo.deleteAllByUser_UserId(user.getUserId());

        // 새로운 관심사 추가
        for (Long categoryId : interests) {
            Category category = categoryRepo.findById(categoryId)
                    .orElseThrow(() -> new CommonException(CommonErrorCode.RESOURCE_NOT_FOUND));
            Interests newInterest = new Interests(user, category);
            interestsRepo.save(newInterest);
        }
        return MyInterestResponseDto.success();
    }
}
