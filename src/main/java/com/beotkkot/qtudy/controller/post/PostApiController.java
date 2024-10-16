package com.beotkkot.qtudy.controller.post;

import com.beotkkot.qtudy.dto.request.posts.PostsRequestDto;
import com.beotkkot.qtudy.dto.response.posts.GetPostsAllResponseDto;
import com.beotkkot.qtudy.dto.response.posts.GetPostsResponseDto;
import com.beotkkot.qtudy.dto.response.posts.PostsResponseDto;
import com.beotkkot.qtudy.dto.response.posts.PutScrapResponseDto;
import com.beotkkot.qtudy.service.posts.PostsService;
import com.beotkkot.qtudy.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PostApiController {

    private final PostsService postsService;
    private final AuthService authService;

    @GetMapping("/posts/all")
    public ResponseEntity<GetPostsAllResponseDto> getAllPost(@RequestParam("page") int page) {
        return postsService.getAllPost(page);
    }

    @PostMapping("/posts")
    public ResponseEntity<PostsResponseDto> save(@RequestHeader(value="Authorization") String token, @RequestBody PostsRequestDto requestDto) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return postsService.savePost(kakao_uid, requestDto);
    }

    @GetMapping("/posts")
    public ResponseEntity<GetPostsResponseDto> getPost(@RequestParam("postId") Long postId) {
        return postsService.getPost(postId);
    }

    @PatchMapping("/posts")
    public ResponseEntity<PostsResponseDto> patchPost(@RequestParam("postId") Long postId, @RequestHeader(value="Authorization") String token, @RequestBody PostsRequestDto requestDto) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return postsService.patchPost(postId, kakao_uid, requestDto);
    }

    @DeleteMapping("/posts")
    public ResponseEntity<PostsResponseDto> deletePost(@RequestParam("postId") Long postId, @RequestHeader(value="Authorization") String token) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return postsService.deletePost(postId, kakao_uid);
    }

    @GetMapping("/posts/my-post-list")
    public ResponseEntity<GetPostsAllResponseDto> getMyPost(@RequestHeader(value="Authorization") String token, @RequestParam("page")int page) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return postsService.getMyPost(kakao_uid, page);
    }

    @GetMapping("/posts/search-list")
    public ResponseEntity<GetPostsAllResponseDto> getSearchPost(@RequestParam("searchWord") String searchWord, @RequestParam("page")int page) {
        return postsService.getSearchPost(searchWord, page);
    }

    @GetMapping("/posts/category-list")
    public ResponseEntity<GetPostsAllResponseDto> searchPostsByCategory(@RequestParam("categoryId") List<Long> categories, @RequestParam("page")int page) {
        return postsService.getCategorySearchPost(categories, page);
    }

    // 스크랩
    @PutMapping("/posts/scrap")
    public ResponseEntity<PutScrapResponseDto> putScrap(@RequestParam("postId") Long postId, @RequestHeader(value="Authorization") String token) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return postsService.putScrap(postId, kakao_uid);
    }

    @GetMapping("/posts/scrap-list")
    public ResponseEntity<GetPostsAllResponseDto> getAllScrapPost(@RequestHeader(value="Authorization") String token, @RequestParam("page")int page) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return postsService.getAllScrapPost(kakao_uid, page);
    }

    @GetMapping("/posts/all-scrap-list")
    public ResponseEntity<GetPostsAllResponseDto> getAllScrapPostNoPage(@RequestHeader(value="Authorization") String token) {
        Long kakao_uid = authService.getKakaoUserInfo(token).getId();
        return postsService.getAllScrapPostNoPage(kakao_uid);
    }
}
