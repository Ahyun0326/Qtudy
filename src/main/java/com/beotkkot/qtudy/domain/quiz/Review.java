package com.beotkkot.qtudy.domain.quiz;

import com.beotkkot.qtudy.domain.user.Users;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private Users user;

    @Column(nullable = false)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizId", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private int userAnswer;

    @Column(nullable = false)
    private String answer;

    @Column(nullable = false)
    private boolean correct;

    private String explanation;

    private Long categoryId;

    private String type;

    private String tags;

    private int score;

    private String createdAt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reviewId;
}
