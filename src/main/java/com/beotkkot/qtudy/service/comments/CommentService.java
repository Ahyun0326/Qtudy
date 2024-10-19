package com.beotkkot.qtudy.service.comments;

import com.beotkkot.qtudy.common.exception.error.CommentErrorCode;
import com.beotkkot.qtudy.common.exception.error.CommonErrorCode;
import com.beotkkot.qtudy.common.exception.error.PostErrorCode;
import com.beotkkot.qtudy.common.exception.error.UserErrorCode;
import com.beotkkot.qtudy.common.exception.exception.CommentException;
import com.beotkkot.qtudy.common.exception.exception.CommonException;
import com.beotkkot.qtudy.common.exception.exception.PostException;
import com.beotkkot.qtudy.common.exception.exception.UserException;
import com.beotkkot.qtudy.domain.comments.Comments;
import com.beotkkot.qtudy.domain.posts.Posts;
import com.beotkkot.qtudy.domain.user.Users;
import com.beotkkot.qtudy.dto.object.CommentListItem;
import com.beotkkot.qtudy.dto.request.comments.CommentsRequestDto;
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
public class CommentService {

    private final CommentsRepository commentRepo;
    private final PostsRepository postRepo;
    private final UserRepository userRepo;

    @Transactional
    public ResponseEntity<CommentsResponseDto> saveComment(Long postId, Long kakaoId, CommentsRequestDto dto) {

        Posts post = postRepo.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.NOT_EXISTED_POST));
        Users user = userRepo.findByKakaoId(kakaoId);
        if (user == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }

        Comments comment = dto.toEntity(post, user);
        commentRepo.save(comment);

        post.updateCommentCount(commentRepo.countByPost_postId(post.getPostId()));

        return CommentsResponseDto.success(userRepo.findByKakaoId(kakaoId).getName(), userRepo.findByKakaoId(kakaoId).getProfileImageUrl());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<GetCommentsAllResponseDto> getAllComment(Long postId, int page) {

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
    public ResponseEntity<CommentsResponseDto> patchComment(Long postId, Long commentId, Long kakaoId, CommentsRequestDto dto) {

        Users user = userRepo.findByKakaoId(kakaoId);
        if (!postRepo.existsById(postId)) {
            throw new PostException(PostErrorCode.NOT_EXISTED_POST);
        } else if (user == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }

        Comments comment = commentRepo.findById(commentId).orElseThrow(() -> new CommentException(CommentErrorCode.NOT_EXISTED_COMMENT));
        if (!comment.getUser().getKakaoId().equals(kakaoId)) {
            throw new CommonException(CommonErrorCode.NO_PERMISSION);
        }
        comment.updateContent(dto.getContent());

        return CommentsResponseDto.success(user.getName(), user.getProfileImageUrl());
    }

    @Transactional
    public ResponseEntity<DeleteCommentsResponseDto> deleteComment(Long postId, Long commentId, Long kakaoId) {
        if (userRepo.findByKakaoId(kakaoId) == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        } else if (!postRepo.existsById(postId)) {
            throw new PostException(PostErrorCode.NOT_EXISTED_POST);
        }

        Comments comment = commentRepo.findById(commentId).orElseThrow(() -> new CommentException(CommentErrorCode.NOT_EXISTED_COMMENT));
        if (!comment.getUser().getKakaoId().equals(kakaoId)) {
            throw new CommonException(CommonErrorCode.NO_PERMISSION);
        }
        commentRepo.delete(comment);
        comment.getPost().updateCommentCount(commentRepo.countByPost_postId(postId));

        return DeleteCommentsResponseDto.success();
    }
}
