package com.beotkkot.qtudy.dto.response.quiz;

import com.beotkkot.qtudy.dto.object.QuizGradeListItem;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class QuizGradeResponseDto {
    private int score;
    private int total_score;
    private List<QuizGradeListItem> gradeList;

    public QuizGradeResponseDto(List<QuizGradeListItem> gradeList, int score) {
        this.score = score;
        this.total_score = 100;
        this.gradeList = gradeList;
    }

    public static ResponseEntity<QuizGradeResponseDto> success(List<QuizGradeListItem> gradeList, int score) {
        QuizGradeResponseDto result = new QuizGradeResponseDto(gradeList, score);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
