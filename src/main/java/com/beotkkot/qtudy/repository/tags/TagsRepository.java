package com.beotkkot.qtudy.repository.tags;


import com.beotkkot.qtudy.domain.tags.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagsRepository extends JpaRepository<Tags, Long> {

    List<Tags> findTagsByNameIn(List<String> tagNames);

    Optional<Tags> findByName(String tagName);

    @Query("SELECT t FROM Tags t ORDER BY t.count DESC LIMIT 3")
    List<Tags> findTop3ByOrderByCountDesc();

    List<Tags> findByCategory_CategoryId(Long categoryId);
}
