package com.beotkkot.qtudy.service.quiz;

import com.beotkkot.qtudy.common.exception.error.PostErrorCode;
import com.beotkkot.qtudy.common.exception.exception.PostException;
import com.beotkkot.qtudy.domain.posts.Posts;
import com.beotkkot.qtudy.domain.quiz.Quiz;
import com.beotkkot.qtudy.domain.quiz.Review;
import com.beotkkot.qtudy.domain.user.Users;
import com.beotkkot.qtudy.dto.object.ReviewDetailListItem;
import com.beotkkot.qtudy.dto.object.ReviewListItem;
import com.beotkkot.qtudy.dto.response.quiz.GetReviewResponseDto;
import com.beotkkot.qtudy.dto.response.quiz.ReviewResponseDto;
import com.beotkkot.qtudy.repository.posts.PostsRepository;
import com.beotkkot.qtudy.repository.quiz.ReviewRepository;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final PostsRepository postRepo;
    private final UserRepository userRepo;

    // 내가 푼 퀴즈 전체 조회
    @Transactional(readOnly = true)
    public ResponseEntity<? super ReviewResponseDto> getMyQuizList(Long kakao_uid, int page) {
        List<ReviewListItem> reviewListItems = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page, 6, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepo.findHighestScoreReviewForEachReviewId(kakao_uid, pageRequest);

        for (Review review : reviews.getContent()) {
            Posts post = postRepo.findById(review.getPostId())
                    .orElseThrow(() -> new PostException(PostErrorCode.NOT_EXISTED_POST));
            Users user = post.getUser();
            int totalScore = reviewRepo.findScoreByUserIdAndReviewId(user.getUserId(), review.getReviewId());
            reviewListItems.add(ReviewListItem.of(user, review, totalScore));
        }
        return ReviewResponseDto.success(reviewListItems, page, reviews.getTotalPages());
    }

    // 내가 푼 퀴즈 상세 조회
    @Transactional(readOnly = true)
    public ResponseEntity<? super GetReviewResponseDto> getMyQuiz(Long kakao_uid, String reviewId) {
        List<ReviewDetailListItem> reviewListItems = new ArrayList<>();
        List<Review> reviews = reviewRepo.findAllReviewByReviewId(reviewId);
        Users user = userRepo.findByKakaoId(kakao_uid);

        for (Review review: reviews) {
            Quiz quiz = review.getQuiz();
            reviewListItems.add(ReviewDetailListItem.of(quiz, review));
        }
        return GetReviewResponseDto.success(reviewListItems, reviewRepo.findScoreByUserIdAndReviewId(user.getUserId(), reviewId));
    }
}
