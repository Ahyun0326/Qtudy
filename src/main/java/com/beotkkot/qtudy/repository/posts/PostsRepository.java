package com.beotkkot.qtudy.repository.posts;

import com.beotkkot.qtudy.domain.posts.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {
    Page<Posts> findAllByUser_KakaoId(Long kakaoId, PageRequest pageRequest);

    @Query("SELECT p FROM Posts p WHERE p.title LIKE %:searchWord% OR p.content LIKE %:searchWord% OR p.tag LIKE %:searchWord%")
    Page<Posts> findBySearchWord(String searchWord, PageRequest pageRequest);

    Page<Posts> findByCategoryIdIn(List<Long> categoryIds, PageRequest pageRequest);
}
