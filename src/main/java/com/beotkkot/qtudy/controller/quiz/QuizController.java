package com.beotkkot.qtudy.controller.quiz;

import com.beotkkot.qtudy.dto.request.quiz.GenerateQuizRequestDto;
import com.beotkkot.qtudy.dto.request.quiz.GradeQuizRequestDto;
import com.beotkkot.qtudy.dto.response.posts.PostsResponseDto;
import com.beotkkot.qtudy.dto.response.quiz.GetPostQuizResponseDto;
import com.beotkkot.qtudy.dto.response.quiz.GetReviewResponseDto;
import com.beotkkot.qtudy.dto.response.quiz.QuizGradeResponseDto;
import com.beotkkot.qtudy.dto.response.quiz.ReviewResponseDto;
import com.beotkkot.qtudy.service.auth.AuthService;
import com.beotkkot.qtudy.service.quiz.QuizService;
import com.beotkkot.qtudy.service.quiz.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class QuizController {

    private final QuizService quizService;
    private final AuthService authService;
    private final ReviewService reviewService;

    // 퀴즈 생성 및 저장
    @PostMapping("/quiz")
    public ResponseEntity<Object> generateQuiz(@RequestBody GenerateQuizRequestDto dto) {
        return ResponseEntity.ok().body(quizService.generateQuiz(dto));
    }

    // 태그별 랜덤 10개 퀴즈 출력
    @GetMapping("/quiz/tag-quiz")
    public ResponseEntity<GetPostQuizResponseDto> getTagQuiz(@RequestParam String tagName) {
        return quizService.getTagQuiz(tagName);
    }

    // 게시글 별 생성된 퀴즈 출력
    @GetMapping("/quiz/post-quiz")
    public ResponseEntity<GetPostQuizResponseDto> getPostQuiz(@RequestParam Long postId) {
        return quizService.getPostQuiz(postId);
    }


    // 정답 채점
    @PostMapping("/quiz/grade")
    public ResponseEntity<QuizGradeResponseDto> gradeQuiz(@RequestHeader(value="Authorization") String token, @RequestBody GradeQuizRequestDto dto) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return quizService.gradeQuiz(dto, kakao_uid);
    }

    // 내가 푼 퀴즈 전체 조회
    @GetMapping("/my/quiz/all")
    public ResponseEntity<ReviewResponseDto> getMyQuizList(@RequestHeader(value="Authorization") String token, @RequestParam int page) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return reviewService.getMyQuizList(kakao_uid, page);
    }

    // 내가 푼 퀴즈 상세 조회
    @GetMapping("/my/quiz")
    public ResponseEntity<GetReviewResponseDto> getMyQuiz(@RequestHeader(value="Authorization") String token, @RequestParam String reviewId) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return reviewService.getMyQuiz(kakao_uid, reviewId);
    }
}
