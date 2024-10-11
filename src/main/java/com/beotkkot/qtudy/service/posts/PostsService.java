package com.beotkkot.qtudy.service.posts;

import com.beotkkot.qtudy.domain.category.Category;
import com.beotkkot.qtudy.domain.posts.Posts;
import com.beotkkot.qtudy.domain.primaryKey.ScrapPk;
import com.beotkkot.qtudy.domain.scrap.Scrap;
import com.beotkkot.qtudy.domain.tags.Tags;
import com.beotkkot.qtudy.domain.user.Users;
import com.beotkkot.qtudy.dto.object.PostListItem;
import com.beotkkot.qtudy.dto.request.posts.PostsRequestDto;
import com.beotkkot.qtudy.dto.response.*;
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
        Long postId;
        List<Tags> newTagList = new ArrayList<>();
        List<String> increasedTag = new ArrayList<>();
        try {
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
                        Category category = categoryRepo.findById(dto.getCategoryId()).orElseThrow();

                        // 새로운 태그인 경우 태그를 생성하고 count를 1로 초기화함
                        Tags newTag = Tags.builder()
                                .name(tagName)
                                .count(1)
                                .category(category)
                                .build();

                        newTagList.add(newTag);
                    }
                }

                // postRepo에 해당 유저가 작성한 글에 대한 요약본 저장하는 부분 추가
                String summary = summaryService.summary(dto.getContent());

                // 포스트 엔티티 생성
                Posts post = dto.toEntity(user, summary);

                // 포스트 저장 후 postId 반환
                Posts savedPost = postsRepo.save(post);
                postId = savedPost.getPostId();

                tagRepo.saveAll(newTagList);
            } else {
                return PostsResponseDto.notExistUser();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
//            for (String tagName : increasedTag) {     FIXME
//                Optional<Tags> existingTag = tagRepo.findByName(tagName);
//                Tags tag = existingTag.get();
//                tag.decreaseTagCount();
//            }
            return ResponseDto.databaseError();
        }
        return PostsResponseDto.success(postId);
    }

    @Transactional
    public ResponseEntity<? super GetPostsResponseDto> getPost(Long postId) {
        Posts post;
        Users user;
        try {
            if (postsRepo.existsById(postId)) {
                post = postsRepo.findById(postId).get();
                user = post.getUser();
            } else {
                return GetPostsResponseDto.noExistPost();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetPostsResponseDto.success(post, user);
    }

    @Transactional
    public ResponseEntity<? super GetSummaryResponseDto> getSummary(Long postId) {
        Posts post;
        String summary;
        try {
            if (postsRepo.existsById(postId)) {
                post = postsRepo.findById(postId).orElseThrow();
                summary = post.getSummary();
            } else {
                return GetSummaryResponseDto.noExistPost();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetSummaryResponseDto.success(postId, summary);
    }

    @Transactional
    public ResponseEntity<? super GetPostsAllResponseDto> getAllPost(int page) {
        List<PostListItem> postListItems = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page, 12, Sort.by("createdAt").descending());
        int totalPages;
        try {
            Page<Posts> posts = postsRepo.findAll(pageRequest);
            totalPages = posts.getTotalPages();
            for (Posts post : posts.getContent())
                postListItems.add(PostListItem.of(post));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetPostsAllResponseDto.success(postListItems, page, totalPages);
    }

    @Transactional
    public ResponseEntity<? super PostsResponseDto> patchPost(Long postId, Long kakao_uid, PostsRequestDto dto) {
        try {
            Optional<Posts> postOptional = postsRepo.findById(postId);
            if (postOptional.isEmpty()) return PostsResponseDto.notExistedPost();
            Posts post = postOptional.get();

            if (userRepo.findByKakaoId(kakao_uid) == null) return PostsResponseDto.notExistUser();
            Long writerId = post.getUser().getKakaoId();
            if (!writerId.equals(kakao_uid)) return PostsResponseDto.noPermission();

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
                        Category category = categoryRepo.findById(dto.getCategoryId()).orElseThrow();
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

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PostsResponseDto.success(postId);
    }

    @Transactional
    public ResponseEntity<? super PostsResponseDto> deletePost(Long postId, Long kakao_uid) {
        Optional<Posts> post = postsRepo.findById(postId);
        try{
            if (post.isEmpty()) return PostsResponseDto.notExistedPost();
            if (userRepo.findByKakaoId(kakao_uid) == null) return PostsResponseDto.notExistUser();

            Long writerId = post.get().getUser().getKakaoId();
            boolean isWriter = writerId.equals(kakao_uid);
            if (!isWriter) return PostsResponseDto.noPermission();

            scrapRepo.deleteByPost_PostId(postId);
            commentsRepo.deleteByPost_PostId(postId);
            reviewRepo.deleteByPostId(postId);
            quizRepo.deleteByPost_PostId(postId);

            // 관련된 hash tag -1
            List<String> tagNameList = Arrays.asList(post.get().getTag().split("\\s*,\\s*"));
            List<Tags> tagList = tagRepo.findByNames(tagNameList);
            for (Tags tag: tagList)
                if (tag.getCount() > 0) tag.decreaseTagCount();
            postsRepo.delete(post.get());

        } catch(Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return PostsResponseDto.success(postId);
    }

    @Transactional
    public ResponseEntity<? super GetPostsAllResponseDto> getMyPost(Long kakao_uid, int page) {
        List<PostListItem> postListItems = new ArrayList<>();
        int totalPages;
        try {
            PageRequest pageRequest = PageRequest.of(page, 12, Sort.by("createdAt").descending());
            Page<Posts> posts = postsRepo.findAllByUser_KakaoId(kakao_uid, pageRequest);
            totalPages = posts.getTotalPages();
            for (Posts post : posts.getContent())
                postListItems.add(PostListItem.of(post));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        GetPostsAllResponseDto responseDto = new GetPostsAllResponseDto(postListItems, page, totalPages);
        return responseDto.success(postListItems, page, totalPages);
    }

    @Transactional
    public ResponseEntity<? super GetPostsAllResponseDto> getSearchPost(String searchWord, int page) {
        List<PostListItem> postListItems = new ArrayList<>();
        int totalPages;
        try {
            PageRequest pageRequest = PageRequest.of(page, 12, Sort.by("createdAt").descending());
            Page<Posts> posts = postsRepo.findBySearchWord(searchWord, pageRequest);
            totalPages = posts.getTotalPages();
            for (Posts post : posts.getContent())
                postListItems.add(PostListItem.of(post));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        GetPostsAllResponseDto responseDto = new GetPostsAllResponseDto(postListItems, page, totalPages);
        return responseDto.success(postListItems, page, totalPages);
    }

    @Transactional
    public ResponseEntity<? super GetPostsAllResponseDto> getCategorySearchPost(List<Long> categories, int page) {
        List<PostListItem> postListItems = new ArrayList<>();
        int totalPages;
        try {
            PageRequest pageRequest = PageRequest.of(page, 12, Sort.by("createdAt").descending());
            Page<Posts> posts = postsRepo.findByCategoryIds(categories, pageRequest);
            totalPages = posts.getTotalPages();

            for (Posts post : posts.getContent()) {
                postListItems.add(PostListItem.of(post));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        GetPostsAllResponseDto responseDto = new GetPostsAllResponseDto(postListItems, page, totalPages);
        return responseDto.success(postListItems, page, totalPages);
    }

    // 스크랩
    @Transactional
    public ResponseEntity<? super PutScrapResponseDto> putScrap(Long postId, Long kakao_uid) {
        try {
            Users user = userRepo.findByKakaoId(kakao_uid);
            if (user == null) return PutScrapResponseDto.notExistUser();
            Posts post = postsRepo.findById(postId).orElseThrow();
            if (post == null) return PutScrapResponseDto.notExistedPost();


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
                Scrap scrap = scrapRepo.findById(scrapId).orElseThrow();
                scrapRepo.delete(scrap);
                post.decreaseScrapCount();
            }

            postsRepo.save(post);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PutScrapResponseDto.success();
    }

    @Transactional
    public ResponseEntity<? super GetPostsAllResponseDto> getAllScrapPost(Long kakao_uid, int page) {
        List<PostListItem> postListItems = new ArrayList<>();
        Users user = userRepo.findByKakaoId(kakao_uid);
        int totalPages;
        try {
            if (user == null) return PutScrapResponseDto.notExistUser();

            PageRequest pageRequest = PageRequest.of(page, 12);
            // 스크랩한 시간 기준으로 내림차순 정렬
            List<Long> postIds = scrapRepo.findAllPostIdByUserId(user.getUserId());
            Page<Posts> posts = postsRepo.findByPostIdIn(postIds, pageRequest);
            totalPages = posts.getTotalPages();

            if (posts == null) return PutScrapResponseDto.notExistedPost();

            for (Posts post : posts.getContent())
                postListItems.add(PostListItem.of(post));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        GetPostsAllResponseDto responseDto = new GetPostsAllResponseDto(postListItems, page, totalPages);
        return responseDto.success(postListItems, page,totalPages);
    }

    @Transactional
    public ResponseEntity<? super GetPostsAllResponseDto> getAllScrapPostNoPage(Long kakao_uid) {
        List<PostListItem> postListItems = new ArrayList<>();
        Users user = userRepo.findByKakaoId(kakao_uid);
        try {
            if (user == null) return PutScrapResponseDto.notExistUser();

            List<Posts> posts = scrapRepo.findAllPostByUserId(user.getUserId());
            if (posts == null) return PutScrapResponseDto.notExistedPost();

            for (Posts post : posts)
                postListItems.add(PostListItem.of(post));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        GetPostsAllResponseDto responseDto = new GetPostsAllResponseDto(postListItems, 0, 0);
        return responseDto.success(postListItems, 0, 0);
    }
}
