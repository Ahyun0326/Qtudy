package com.beotkkot.qtudy.service.comments;

import com.beotkkot.qtudy.domain.comments.Comments;
import com.beotkkot.qtudy.domain.posts.Posts;
import com.beotkkot.qtudy.domain.user.Users;
import com.beotkkot.qtudy.dto.object.CommentListItem;
import com.beotkkot.qtudy.dto.request.comments.CommentsRequestDto;
import com.beotkkot.qtudy.dto.response.ResponseDto;
import com.beotkkot.qtudy.dto.response.comments.CommentsResponseDto;
import com.beotkkot.qtudy.dto.response.comments.DeleteCommentsResponseDto;
import com.beotkkot.qtudy.dto.response.comments.GetCommentsAllResponseDto;
import com.beotkkot.qtudy.repository.comments.CommentsRepository;
import com.beotkkot.qtudy.repository.posts.PostsRepository;
import com.beotkkot.qtudy.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentsRepository commentRepo;
    private final PostsRepository postRepo;
    private final UserRepository userRepo;

    @Transactional
    public ResponseEntity<? super CommentsResponseDto> saveComment(Long postId, Long kakaoId, CommentsRequestDto dto) {

        try {
            if (!postRepo.existsById(postId)) {
                // 존재하지 않는 포스트
                return CommentsResponseDto.notExistedPost();
            } else if (userRepo.findByKakaoId(kakaoId) == null) {
                // 존재하지 않는 유저
                return CommentsResponseDto.notExistedUser();
            } else {
                // 댓글 엔티티 생성
                Users user = userRepo.findByKakaoId(kakaoId);
                Posts post = postRepo.findById(postId).orElseThrow();
                Comments comment = dto.toEntity(post, user);

                // 댓글 저장
                commentRepo.save(comment);

                post.updateCommentCount(commentRepo.countByPost_postId(post.getPostId()));
            }
        } catch (Exception exception) {
            log.info("error " + exception.getMessage());
            return ResponseDto.databaseError();
        }
        return CommentsResponseDto.success(userRepo.findByKakaoId(kakaoId).getName(), userRepo.findByKakaoId(kakaoId).getProfileImageUrl());
    }

    public ResponseEntity<? super GetCommentsAllResponseDto> getAllComment(Long postId, int page) {

        Pageable pageable = PageRequest.of(page, 4, Sort.by("createdAt").descending());
        List<Comments> comments = commentRepo.findByPost_PostId(postId, pageable).getContent();

        List<CommentListItem> commentListItems = new ArrayList<>();
        for (Comments comment : comments) {
            Users user = comment.getUser();
            commentListItems.add(CommentListItem.of(comment, user));
        }

        return GetCommentsAllResponseDto.success(commentListItems, page);
    }

    @Transactional
    public ResponseEntity<? super CommentsResponseDto> patchComment(Long postId, Long commentId, Long kakaoId, CommentsRequestDto dto) {

        Users user = userRepo.findByKakaoId(kakaoId);
        try {
            if (user == null) {
                return CommentsResponseDto.notExistedUser();
            } else if (!postRepo.existsById(postId)) {
                return CommentsResponseDto.notExistedPost();
            } else if (!commentRepo.existsById(commentId)) {
                return CommentsResponseDto.notExistedComment();
            } else {
                Comments comment = commentRepo.findById(commentId).orElseThrow();
                if (!comment.getUser().getKakaoId().equals(kakaoId)) {
                    return CommentsResponseDto.noPermission();
                }
                comment.updateContent(dto.getContent());
            }
        } catch (Exception exception) {
            log.info("error " + exception.getMessage());
            return ResponseDto.databaseError();
        }
        return CommentsResponseDto.success(user.getName(), user.getProfileImageUrl());
    }

    @Transactional
    public ResponseEntity<? super DeleteCommentsResponseDto> deleteComment(Long postId, Long commentId, Long kakaoId) {
        try {
            if (userRepo.findByKakaoId(kakaoId) == null) {
                return DeleteCommentsResponseDto.notExistedUser();
            } else if (!postRepo.existsById(postId)) {
                return DeleteCommentsResponseDto.notExistedPost();
            } else if (!commentRepo.existsById(commentId)) {
                return DeleteCommentsResponseDto.notExistedComment();
            } else {
                // 댓글 삭제
                Comments comment = commentRepo.findById(commentId).orElseThrow();
                if (!comment.getUser().getKakaoId().equals(kakaoId)) {
                    return DeleteCommentsResponseDto.noPermission();
                }
                commentRepo.delete(comment);

                // commentCount 업데이트
                comment.getPost().updateCommentCount(commentRepo.countByPost_postId(postId));
            }
        } catch (Exception exception) {
            log.info("error " + exception.getMessage());
            return ResponseDto.databaseError();
        }
        return DeleteCommentsResponseDto.success();
    }
}
