package com.beotkkot.qtudy.dto.response.tags;

import com.beotkkot.qtudy.dto.object.TagListItem;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetTagsResponseDto {
    private List<TagListItem> tagList;

    public GetTagsResponseDto(List<TagListItem> TagListItem) {
        this.tagList = TagListItem;
    }

    public static ResponseEntity<GetTagsResponseDto> success(List<TagListItem> tagList) {
        GetTagsResponseDto result = new GetTagsResponseDto(tagList);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
