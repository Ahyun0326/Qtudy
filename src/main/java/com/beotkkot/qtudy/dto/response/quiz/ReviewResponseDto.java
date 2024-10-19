package com.beotkkot.qtudy.dto.response.quiz;

import com.beotkkot.qtudy.dto.object.ReviewListItem;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class ReviewResponseDto {
    private int page;
    private int totalPages;
    private List<ReviewListItem> reviewListItems;

    public ReviewResponseDto(List<ReviewListItem> reviewListItems, int page, int totalPages) {
        this.page = page;
        this.totalPages = totalPages;
        this.reviewListItems = reviewListItems;
    }

    public static ResponseEntity<ReviewResponseDto> success(List<ReviewListItem> reviewListItems, int page, int totalPages) {
        ReviewResponseDto result = new ReviewResponseDto(reviewListItems, page, totalPages);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
