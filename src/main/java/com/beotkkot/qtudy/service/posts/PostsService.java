package com.beotkkot.qtudy.service.posts;

import com.beotkkot.qtudy.common.exception.error.CommonErrorCode;
import com.beotkkot.qtudy.common.exception.error.PostErrorCode;
import com.beotkkot.qtudy.common.exception.error.UserErrorCode;
import com.beotkkot.qtudy.common.exception.exception.CommonException;
import com.beotkkot.qtudy.common.exception.exception.PostException;
import com.beotkkot.qtudy.common.exception.exception.UserException;
import com.beotkkot.qtudy.domain.category.Category;
import com.beotkkot.qtudy.domain.posts.Posts;
import com.beotkkot.qtudy.domain.primaryKey.ScrapPk;
import com.beotkkot.qtudy.domain.scrap.Scrap;
import com.beotkkot.qtudy.domain.tags.Tags;
import com.beotkkot.qtudy.domain.user.Users;
import com.beotkkot.qtudy.dto.object.PostListItem;
import com.beotkkot.qtudy.dto.request.posts.PostsRequestDto;
import com.beotkkot.qtudy.dto.response.posts.*;
import com.beotkkot.qtudy.repository.category.CategoryRepository;
import com.beotkkot.qtudy.repository.comments.CommentsRepository;
import com.beotkkot.qtudy.repository.posts.PostsRepository;
import com.beotkkot.qtudy.repository.quiz.QuizRepository;
import com.beotkkot.qtudy.repository.quiz.ReviewRepository;
import com.beotkkot.qtudy.repository.scrap.ScrapRepository;
import com.beotkkot.qtudy.repository.tags.TagsRepository;
import com.beotkkot.qtudy.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepo;
    private final UserRepository userRepo;
    private final TagsRepository tagRepo;
    private final ScrapRepository scrapRepo;
    private final SummaryService summaryService;
    private final CommentsRepository commentsRepo;
    private final ReviewRepository reviewRepo;
    private final QuizRepository quizRepo;
    private final CategoryRepository categoryRepo;

    @Transactional
    public ResponseEntity<? super PostsResponseDto> savePost(Long kakao_uid, PostsRequestDto dto) {
        List<Tags> newTagList = new ArrayList<>();
        List<String> increasedTag = new ArrayList<>();

        Users user = userRepo.findByKakaoId(kakao_uid);
        if (user != null) {
            // 태그 처리
            List<String> postTags = dto.getTag();

            for (String tagName : postTags) {
                Optional<Tags> existingTag = tagRepo.findByName(tagName);
                if (existingTag.isPresent()) {
                    // 기존에 있는 태그인 경우 count를 증가시킴
                    Tags tag = existingTag.get();
                    tag.increaseTagCount();
                    increasedTag.add(tagName);
                } else {
                    Category category = categoryRepo.findById(dto.getCategoryId())
                            .orElseThrow(() -> new CommonException(CommonErrorCode.RESOURCE_NOT_FOUND));
                    // 새로운 태그인 경우 태그를 생성하고 count를 1로 초기화함
                    Tags newTag = Tags.builder()
                            .name(tagName)
                            .count(1)
                            .category(category)
                            .build();
                    newTagList.add(newTag);
                }
            }

            String summary = summaryService.summary(dto.getContent());
            Posts post = dto.toEntity(user, summary);
            Posts savedPost = postsRepo.save(post);
            tagRepo.saveAll(newTagList);

            return PostsResponseDto.success(savedPost.getPostId());
        } else {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<? super GetPostsResponseDto> getPost(Long postId) {
        Posts post = postsRepo.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.NOT_EXISTED_POST));
        Users user = post.getUser();

        return GetPostsResponseDto.success(post, user);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<? super GetSummaryResponseDto> getSummary(Long postId) {
        Posts post = postsRepo.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.NOT_EXISTED_POST));
        String summary = post.getSummary();

        return GetSummaryResponseDto.success(postId, summary);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<? super GetPostsAllResponseDto> getAllPost(int page) {
        List<PostListItem> postListItems = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page, 12, Sort.by("createdAt").descending());
        Page<Posts> posts = postsRepo.findAll(pageRequest);
        for (Posts post : posts.getContent())
            postListItems.add(PostListItem.of(post));

        return GetPostsAllResponseDto.success(postListItems, page, posts.getTotalPages());
    }

    @Transactional
    public ResponseEntity<? super PostsResponseDto> patchPost(Long postId, Long kakao_uid, PostsRequestDto dto) {
        Posts post = postsRepo.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.NOT_EXISTED_POST));
        if (userRepo.findByKakaoId(kakao_uid) == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }
        if (!post.getUser().getKakaoId().equals(kakao_uid)) {
            throw new CommonException(CommonErrorCode.NO_PERMISSION);
        }

        // 업데이트되기 이전의 태그 목록
        List<String> existingTags = Arrays.asList(post.getTag().split(","));
        // 업데이트된 태그 목록
        List<String> updatedTags = dto.getTag();

        // 태그 카운트를 증감시키기 위한 로직
        for (String tagName : existingTags) {
            if (!updatedTags.contains(tagName)) {
                // 태그가 삭제된 경우 카운트 감소
                Optional<Tags> tagOptional = tagRepo.findByName(tagName);
                tagOptional.ifPresent(Tags::decreaseTagCount);
            }
        }
        for (String tagName : updatedTags) {
            if (!existingTags.contains(tagName)) {
                Optional<Tags> existTag = tagRepo.findByName(tagName);
                if (existTag.isPresent()) {
                    // 기존에 있는 태그인 경우 count를 증가시킴
                    Tags tag = existTag.get();
                    tag.increaseTagCount();
                } else {
                    Category category = categoryRepo.findById(dto.getCategoryId())
                            .orElseThrow(() -> new CommonException(CommonErrorCode.RESOURCE_NOT_FOUND));
                    // 새로운 태그인 경우 태그를 생성하고 count를 1로 초기화함
                    Tags newTag = Tags.builder()
                            .name(tagName)
                            .count(1)
                            .category(category)
                            .build();
                    // 새로운 태그를 저장
                    tagRepo.save(newTag);
                }
            }
        }

        // 요약
        String summary = summaryService.summary(dto.getContent());
        post.patchPost(dto, summary);
        postsRepo.save(post);

        return PostsResponseDto.success(postId);
    }

    @Transactional
    public ResponseEntity<? super PostsResponseDto> deletePost(Long postId, Long kakao_uid) {
        Posts post = postsRepo.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.NOT_EXISTED_POST));
        if (userRepo.findByKakaoId(kakao_uid) == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }
        boolean isWriter = post.getUser().getKakaoId().equals(kakao_uid);
        if (!isWriter) {
            throw new CommonException(CommonErrorCode.NO_PERMISSION);
        }

        scrapRepo.deleteByPost_PostId(postId);
        commentsRepo.deleteByPost_PostId(postId);
        reviewRepo.deleteByPostId(postId);
        quizRepo.deleteByPost_PostId(postId);

        // 관련된 hash tag -1
        List<String> tagNameList = Arrays.asList(post.getTag().split("\\s*,\\s*"));
        List<Tags> tagList = tagRepo.findTagsByNameIn(tagNameList);
        for (Tags tag: tagList)
            if (tag.getCount() > 0) tag.decreaseTagCount();
        postsRepo.delete(post);

        return PostsResponseDto.success(postId);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<? super GetPostsAllResponseDto> getMyPost(Long kakao_uid, int page) {
        List<PostListItem> postListItems = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page, 12, Sort.by("createdAt").descending());

        Page<Posts> posts = postsRepo.findAllByUser_KakaoId(kakao_uid, pageRequest);
        for (Posts post : posts.getContent())
            postListItems.add(PostListItem.of(post));

        return GetPostsAllResponseDto.success(postListItems, page,  posts.getTotalPages());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<? super GetPostsAllResponseDto> getSearchPost(String searchWord, int page) {
        List<PostListItem> postListItems = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page, 12, Sort.by("createdAt").descending());

        Page<Posts> posts = postsRepo.findBySearchWord(searchWord, pageRequest);
        for (Posts post : posts.getContent())
            postListItems.add(PostListItem.of(post));

        return GetPostsAllResponseDto.success(postListItems, page, posts.getTotalPages());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<? super GetPostsAllResponseDto> getCategorySearchPost(List<Long> categories, int page) {
        List<PostListItem> postListItems = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page, 12, Sort.by("createdAt").descending());

        Page<Posts> posts = postsRepo.findByCategoryIdIn(categories, pageRequest);
        for (Posts post : posts.getContent()) {
            postListItems.add(PostListItem.of(post));
        }

        return GetPostsAllResponseDto.success(postListItems, page, posts.getTotalPages());
    }

    // 스크랩
    @Transactional
    public ResponseEntity<? super PutScrapResponseDto> putScrap(Long postId, Long kakao_uid) {
        Users user = userRepo.findByKakaoId(kakao_uid);
        if (user == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }
        Posts post = postsRepo.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.NOT_EXISTED_POST));

        Date now = Date.from(Instant.now());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String scrapDatetime = simpleDateFormat.format(now);

        ScrapPk scrapId = new ScrapPk(user.getUserId(), postId);

        // 존재하지 않는다면 추가. 존재한다면 삭제
        if (!scrapRepo.existsById(scrapId)) {
            Scrap scrap = new Scrap(user, post, scrapDatetime);
            scrapRepo.save(scrap);
            post.increaseScrapCount();
        } else {
            Scrap scrap = scrapRepo.findById(scrapId).orElseThrow(() -> new CommonException(CommonErrorCode.RESOURCE_NOT_FOUND));
            scrapRepo.delete(scrap);
            post.decreaseScrapCount();
        }
        postsRepo.save(post);

        return PutScrapResponseDto.success();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<? super GetPostsAllResponseDto> getAllScrapPost(Long kakao_uid, int page) {
        List<PostListItem> postListItems = new ArrayList<>();
        Users user = userRepo.findByKakaoId(kakao_uid);
        if (user == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }

        PageRequest pageRequest = PageRequest.of(page, 12);

        // 스크랩한 시간 기준으로 내림차순 정렬
        Page<Posts> posts = scrapRepo.findScrapPostsByUserId(user.getUserId(), pageRequest);
        for (Posts post : posts.getContent())
            postListItems.add(PostListItem.of(post));

        return GetPostsAllResponseDto.success(postListItems, page, posts.getTotalPages());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<? super GetPostsAllResponseDto> getAllScrapPostNoPage(Long kakao_uid) {
        List<PostListItem> postListItems = new ArrayList<>();
        Users user = userRepo.findByKakaoId(kakao_uid);
        if (user == null) {
            throw new UserException(UserErrorCode.NOT_EXISTED_USER);
        }

        List<Posts> posts = scrapRepo.findAllPostByUserId(user.getUserId());
        if (posts == null) {
            throw new PostException(PostErrorCode.NOT_EXISTED_POST);
        }
        for (Posts post : posts)
            postListItems.add(PostListItem.of(post));

        return GetPostsAllResponseDto.success(postListItems, 0, 0);
    }
}
