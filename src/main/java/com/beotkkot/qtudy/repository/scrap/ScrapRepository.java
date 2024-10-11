package com.beotkkot.qtudy.repository.scrap;

import com.beotkkot.qtudy.domain.posts.Posts;
import com.beotkkot.qtudy.domain.scrap.Scrap;
import com.beotkkot.qtudy.domain.primaryKey.ScrapPk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, ScrapPk> {

    void deleteByPost_PostId(Long postId);

    @Query("SELECT s.post FROM Scrap s WHERE s.user.userId = :userId ORDER BY s.scrapAt DESC")
    List<Posts> findAllPostByUserId(@Param("userId") Long userId);

    @Query("SELECT s.post FROM Scrap s where s.user.userId = :userId order by s.scrapAt DESC")
    Page<Posts> findScrapPostsByUserId(@Param("userId") Long userId, PageRequest pageRequest);
}
