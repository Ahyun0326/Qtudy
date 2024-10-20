package com.beotkkot.qtudy.dto.response.comments;

import com.beotkkot.qtudy.dto.object.CommentListItem;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;


@Getter
public class GetCommentsAllResponseDto {
    private int page;
    private List<CommentListItem> commentList;

    public GetCommentsAllResponseDto(List<CommentListItem> commentListItem, int page) {
        this.page = page;
        this.commentList = commentListItem;
    }

    public static ResponseEntity<GetCommentsAllResponseDto> success(List<CommentListItem> commentListItem, int page) {
        GetCommentsAllResponseDto result = new GetCommentsAllResponseDto(commentListItem, page);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}



