package com.beotkkot.qtudy.dto.response.mypage;

import com.beotkkot.qtudy.dto.object.PostListItem;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;


@Getter
public class GetMyPageAllResponseDto {
    private int page;
    private int totalPages;
    private List<PostListItem> postList;

    public GetMyPageAllResponseDto(List<PostListItem> postListItem, int page, int totalPages) {
        this.page = page;
        this.totalPages = totalPages;
        this.postList = postListItem;
    }

    public static ResponseEntity<GetMyPageAllResponseDto> success(List<PostListItem> postListItem, int page, int totalPages) {
        GetMyPageAllResponseDto result = new GetMyPageAllResponseDto(postListItem, page, totalPages);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}



