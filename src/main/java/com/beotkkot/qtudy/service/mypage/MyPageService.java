package com.beotkkot.qtudy.service.mypage;

import com.beotkkot.qtudy.domain.category.Category;
import com.beotkkot.qtudy.domain.interests.Interests;
import com.beotkkot.qtudy.domain.posts.Posts;
import com.beotkkot.qtudy.domain.user.Users;
import com.beotkkot.qtudy.dto.object.PostListItem;
import com.beotkkot.qtudy.dto.response.ResponseDto;
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
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final PostsRepository postRepo;
    private final UserRepository userRepo;
    private final ScrapRepository scrapRepo;
    private final CategoryRepository categoryRepo;
    private final InterestsRepository interestsRepo;

    // 관심사 저장
    @Transactional
    public ResponseEntity<? super MyInterestResponseDto> saveMyInterests(Long kakao_uid, List<Long> interests) {

        try {
            Users user = userRepo.findByKakaoId(kakao_uid);
            if (user == null) return MyInterestResponseDto.notExistedUser();
            List<Interests> findInterests = interestsRepo.findByUser_UserId(user.getUserId());
            if ((interests.size() > 3) || (!findInterests.isEmpty())) return MyInterestResponseDto.inputFailed();

            for (Long catetoryId : interests) {
                // 사용자의 관심사 3개를 interestsRepo에 저장한다.
                // 1. CategoryRepo에서 사용자가 선택한 interests에 해당되는 관심사 조회
                Optional<Category> category = categoryRepo.findById(catetoryId);
                // 존재하지 않는 카테고리일 경우
                if (category.isEmpty()) return MyInterestResponseDto.databaseError();
                Interests interest = new Interests(user, category.get());
                interestsRepo.save(interest);
            }
        } catch (Exception exception) {
            log.info("error ", exception.getMessage());
            return MyInterestResponseDto.databaseError();
        }
        return MyInterestResponseDto.success();
    }

    // 관심 분야 목록 조회
    public ResponseEntity<? super GetMyInterestResponseDto> getMyInterests(Long kakao_uid) {

        List<Long> interestIds = new ArrayList<>();
        try {
            Users user = userRepo.findByKakaoId(kakao_uid);
            if (user == null) return GetMyInterestResponseDto.notExistedUser();
            List<Interests> interests = interestsRepo.findByUser_UserId(user.getUserId());
            for (Interests interest : interests) {
                interestIds.add(interest.getCategory().getCategoryId());
            }
        } catch (Exception exception) {
            log.info("error ", exception.getMessage());
            return GetMyInterestResponseDto.databaseError();
        }

        return GetMyInterestResponseDto.success(interestIds);
    }

    // 마이페이지에서 자신의 정보 조회 (관심사, 사용자 이름, 이메일, 사용자 프로필이미지)
    public ResponseEntity<? super GetMyPageInfoResponseDto> getMyPageInfo(Long kakao_uid, String email) {

        Users user = userRepo.findByKakaoId(kakao_uid);
        try {
            // 유저가 존재하지 않으면 에러
            if (user == null) return GetMyPageInfoResponseDto.notExistedUser();
        } catch (Exception exception) {
            log.info("error " + exception.getMessage());
            return GetMyPageInfoResponseDto.databaseError();
        }
        return GetMyPageInfoResponseDto.success(user, email);
    }

    public ResponseEntity<? super GetMyPageAllResponseDto> getAllPost(Long kakao_uid, int page) {
        List<PostListItem> postListItems = new ArrayList<>();
        int totalPages;
        try {
            PageRequest pageRequest = PageRequest.of(page, 6, Sort.by("createdAt").descending());
            Page<Posts> posts = postRepo.findAllByUser_KakaoId(kakao_uid, pageRequest);
            totalPages = posts.getTotalPages();
            for (Posts post : posts.getContent())
                postListItems.add(PostListItem.of(post));
        } catch (Exception exception) {
            log.info("error " + exception.getMessage());
            return ResponseDto.databaseError();
        }

        return GetMyPageAllResponseDto.success(postListItems, page, totalPages);
    }

    public ResponseEntity<? super GetMyPageAllResponseDto> getAllScrapPost(Long kakao_uid, int page) {

        List<PostListItem> postListItems = new ArrayList<>();
        Users user = userRepo.findByKakaoId(kakao_uid);
        int totalPages;
        try {
            if (user == null) return GetMyPageAllResponseDto.notExistedUser();

            PageRequest pageRequest = PageRequest.of(page, 6);

            // 유저 아이디 -> 스크랩(유저가 스크랩한 글 리스트) -> 포스트 -> 포스트 페이지네이션(스크랩한 시간대로 내림차순)
            Page<Posts> posts = scrapRepo.findScrapPostsByUserId(user.getUserId(), pageRequest);
            totalPages = posts.getTotalPages();

            for (Posts post : posts.getContent())
                postListItems.add(PostListItem.of(post));
        } catch (Exception exception) {
            log.info("error " + exception.getMessage());
            return ResponseDto.databaseError();
        }

        return GetMyPageAllResponseDto.success(postListItems, page, totalPages);
    }

    @Transactional
    public ResponseEntity<? super MyInterestResponseDto> patchMyInterests(Long kakao_uid, List<Long> interests) {
        try {
            Users user = userRepo.findByKakaoId(kakao_uid);
            if (user == null) return MyInterestResponseDto.notExistedUser();
            if (interests.size() > 3) return MyInterestResponseDto.inputFailed();

            // 기존의 관심사를 모두 삭제
            interestsRepo.deleteAllByUser_UserId(user.getUserId());

            // 새로운 관심사 추가
            for (Long categoryId : interests) {
                Optional<Category> category = categoryRepo.findById(categoryId);
                if (category.isEmpty()) { // 존재하지 않는 카테고리일 경우
                    return MyInterestResponseDto.databaseError();
                }
                Interests newInterest = new Interests(user, category.get());
                interestsRepo.save(newInterest);
            }
        } catch (Exception exception) {
            log.info("error ", exception.getMessage());
            return MyInterestResponseDto.databaseError();
        }
        return MyInterestResponseDto.success();
    }
}
