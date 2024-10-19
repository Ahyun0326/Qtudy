package com.beotkkot.qtudy.dto.response.quiz;

import com.beotkkot.qtudy.dto.object.ReviewDetailListItem;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetReviewResponseDto {
    private int totalScore;
    private List<ReviewDetailListItem> reviewList;

    public GetReviewResponseDto(List<ReviewDetailListItem> reviewList, int totalScore) {
        this.totalScore = totalScore;
        this.reviewList = reviewList;
    }

    public static ResponseEntity<GetReviewResponseDto> success(List<ReviewDetailListItem> reviewList, int totalScore) {
        GetReviewResponseDto result = new GetReviewResponseDto(reviewList, totalScore);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
