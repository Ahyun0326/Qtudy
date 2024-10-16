package com.beotkkot.qtudy.dto.response.quiz;

import com.beotkkot.qtudy.dto.object.QuizListItem;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetPostQuizResponseDto {
    private String type;
    private List<String> answerList;
    private List<Long> quizIdList;
    private List<QuizListItem> quizList;

    public GetPostQuizResponseDto(List<QuizListItem> QuizListItem, List<String> AnswerListItem, List<Long> quizIdList, String type) {
        this.type = type;
        this.answerList = AnswerListItem;
        this.quizIdList = quizIdList;
        this.quizList = QuizListItem;
    }

    public static ResponseEntity<GetPostQuizResponseDto> success(List<QuizListItem> QuizListItem, List<String> AnswerListItem, List<Long> quizIdList, String type) {
        GetPostQuizResponseDto result = new GetPostQuizResponseDto(QuizListItem, AnswerListItem, quizIdList, type);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}