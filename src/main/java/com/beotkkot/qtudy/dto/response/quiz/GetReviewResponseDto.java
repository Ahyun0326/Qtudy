package com.beotkkot.qtudy.dto.response.quiz;

import com.beotkkot.qtudy.common.ResponseCode;
import com.beotkkot.qtudy.common.ResponseMessage;
import com.beotkkot.qtudy.dto.object.ReviewDetailListItem;
import com.beotkkot.qtudy.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetReviewResponseDto extends ResponseDto {
    private int totalScore;
    private List<ReviewDetailListItem> reviewList;

    public GetReviewResponseDto(List<ReviewDetailListItem> reviewList, int totalScore) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.totalScore = totalScore;
        this.reviewList = reviewList;
    }

    public static ResponseEntity<GetReviewResponseDto> success(List<ReviewDetailListItem> reviewList, int totalScore) {
        GetReviewResponseDto result = new GetReviewResponseDto(reviewList, totalScore);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
