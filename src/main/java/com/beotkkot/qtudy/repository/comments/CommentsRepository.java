package com.beotkkot.qtudy.repository.comments;

import com.beotkkot.qtudy.domain.comments.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<Comments, Long> {

    Page<Comments> findByPost_PostId(Long postId, Pageable pageable);

    int countByPost_postId(Long postId);

    void deleteByPost_PostId(Long postId);
}
