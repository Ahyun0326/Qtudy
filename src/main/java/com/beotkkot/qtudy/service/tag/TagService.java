package com.beotkkot.qtudy.service.tag;

import com.beotkkot.qtudy.domain.tags.Tags;
import com.beotkkot.qtudy.dto.object.TagListItem;
import com.beotkkot.qtudy.dto.response.tags.GetTagsResponseDto;
import com.beotkkot.qtudy.repository.tags.TagsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TagService {

    private final TagsRepository tagRepo;

    @Transactional(readOnly = true)
    public ResponseEntity<? super GetTagsResponseDto> getTop3Tags() {
        List<TagListItem> top3List = new ArrayList<>();
        List<Tags> tags = tagRepo.findTop3ByOrderByCountDesc();
        for (Tags tag : tags) {
            if (tag.getCount() > 0) top3List.add(TagListItem.of(tag));
        }
        log.info(String.valueOf(top3List));

        return GetTagsResponseDto.success(top3List);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<? super GetTagsResponseDto> getTagsByCategory(Long categoryId) {
        List<TagListItem> tagList = new ArrayList<>();
        List<Tags> tags = tagRepo.findByCategory_CategoryId(categoryId);
        for (Tags tag : tags) {
            if (tag.getCount() > 0) tagList.add(TagListItem.of(tag));
        }

        return GetTagsResponseDto.success(tagList);
    }
}
