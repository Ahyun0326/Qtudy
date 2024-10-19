package com.beotkkot.qtudy.controller.comment;

import com.beotkkot.qtudy.dto.request.comments.CommentsRequestDto;
import com.beotkkot.qtudy.dto.response.comments.CommentsResponseDto;
import com.beotkkot.qtudy.dto.response.comments.DeleteCommentsResponseDto;
import com.beotkkot.qtudy.dto.response.comments.GetCommentsAllResponseDto;
import com.beotkkot.qtudy.service.auth.AuthService;
import com.beotkkot.qtudy.service.comments.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final AuthService authService;

    // 전체 댓글 조회
    @GetMapping("/posts/comments/all")
    public ResponseEntity<GetCommentsAllResponseDto> getAllComment(@RequestParam("postId") Long postId, @RequestParam("page") int page) {
        return commentService.getAllComment(postId, page);
    }

    // 댓글 저장
    @PostMapping("/posts/comments")
    public ResponseEntity<CommentsResponseDto> save(@RequestParam("postId") Long postId, @RequestHeader("Authorization") String token, @RequestBody CommentsRequestDto requestDto) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return commentService.saveComment(postId, kakao_uid, requestDto);
    }

    // 댓글 수정
    @PatchMapping("/posts/comments")
    public ResponseEntity<CommentsResponseDto> patchComment(@RequestParam("postId") Long postId, @RequestParam("commentId") Long commentId,
                             @RequestHeader("Authorization") String token, @RequestBody CommentsRequestDto requestDto) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return commentService.patchComment(postId, commentId, kakao_uid, requestDto);
    }

    // 댓글 삭제
    @DeleteMapping("/posts/comments")
    public ResponseEntity<DeleteCommentsResponseDto> deleteComment(@RequestParam("postId") Long postId, @RequestParam("commentId") Long commentId, @RequestHeader("Authorization") String token) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return commentService.deleteComment(postId, commentId, kakao_uid);
    }
}
