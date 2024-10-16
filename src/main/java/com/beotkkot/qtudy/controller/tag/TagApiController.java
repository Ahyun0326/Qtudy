package com.beotkkot.qtudy.controller.tag;

import com.beotkkot.qtudy.dto.response.tags.GetTagsResponseDto;
import com.beotkkot.qtudy.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TagApiController {
    private final TagService tagService;

    @GetMapping("/tag/top3")
    public ResponseEntity<GetTagsResponseDto> getTop3Tags() {
        return tagService.getTop3Tags();
    }

    @GetMapping("/tag")
    public ResponseEntity<GetTagsResponseDto> getTagsByCategory(@RequestParam("categoryId") Long categoryId) {
        return tagService.getTagsByCategory(categoryId);
    }
}
