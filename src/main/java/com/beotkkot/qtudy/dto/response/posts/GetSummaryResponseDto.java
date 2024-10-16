package com.beotkkot.qtudy.dto.response.posts;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetSummaryResponseDto {
    private Long postId;
    private String summary;

    @Builder
    private GetSummaryResponseDto(Long postId, String summary) {
        this.postId = postId;
        this.summary = summary;
    }

    public static ResponseEntity<GetSummaryResponseDto> success(Long postId, String summary) {

        GetSummaryResponseDto result = new GetSummaryResponseDto(postId, summary);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
