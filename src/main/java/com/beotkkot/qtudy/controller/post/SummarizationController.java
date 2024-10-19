package com.beotkkot.qtudy.controller.post;

import com.beotkkot.qtudy.dto.response.posts.GetSummaryResponseDto;
import com.beotkkot.qtudy.service.posts.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SummarizationController {

    private final PostsService postsService;

    @GetMapping("/summary")
    public ResponseEntity<GetSummaryResponseDto> summary(@RequestParam("postId") Long postId) {
        return postsService.getSummary(postId);
    }
}
