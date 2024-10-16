package com.beotkkot.qtudy.dto.response.posts;

import com.beotkkot.qtudy.dto.object.PostListItem;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;


@Getter
public class GetPostsAllResponseDto {
    private int page;
    private int totalPages;
    private List<PostListItem> postList;

    public GetPostsAllResponseDto(List<PostListItem> PostListItem, int page, int totalPages) {
        this.page = page;
        this.totalPages = totalPages;
        this.postList = PostListItem;
    }

    public static ResponseEntity<GetPostsAllResponseDto> success(List<PostListItem> PostListItem, int page, int totalPages) {
        GetPostsAllResponseDto result = new GetPostsAllResponseDto(PostListItem, page, totalPages);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}



