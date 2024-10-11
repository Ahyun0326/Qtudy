package com.beotkkot.qtudy.repository.quiz;

import com.beotkkot.qtudy.domain.quiz.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r " +
            "WHERE r.user.kakaoId = :kakaoId " +
            "GROUP BY r.reviewId " +
            "ORDER BY MAX(r.score) DESC")
    Page<Review> findHighestScoreReviewForEachReviewId(@Param("kakaoId") Long kakaoId, PageRequest pageRequest);

    // reviewId별 totalScore 구하기
    @Query("SELECT SUM(r.score) " +
            "FROM Review r " +
            "WHERE r.user.userId = :userId AND r.reviewId = :reviewId")
    int findScoreByUserIdAndReviewId(@Param("userId") Long userId, @Param("reviewId") String reviewId);

    List<Review> findAllReviewByReviewId(String reviewId);

    void deleteByPostId(Long postId);
}
