package com.beotkkot.qtudy.domain.quiz;

import com.beotkkot.qtudy.domain.posts.Posts;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    private Posts post;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tags;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(/*nullable = false,*/  columnDefinition = "TEXT")
    private String answer;

    @Column(/*nullable = false,*/  columnDefinition = "TEXT")
    private String explanation;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String options;
}
