package com.example.kviz.service;

import java.util.List;

import com.example.kviz.DTO.CreateQuizDTO;
import com.example.kviz.DTO.GetQuizDTO;
import com.example.kviz.repository.QuizAggregateRepository;

public class QuizCRUDService {
    private final QuizAggregateRepository quizAggregateRepository = new QuizAggregateRepository();
    public void createQuiz(CreateQuizDTO quizRaw) {
        quizAggregateRepository.createQuiz(quizRaw);
    }
    public void updateQuiz(CreateQuizDTO quizRaw) {
        quizAggregateRepository.updateQuiz(quizRaw);
    }
    public void deleteQuiz(Long quizId) {
        quizAggregateRepository.deleteQuiz(quizId);
    }
    public GetQuizDTO getQuizById(Long quizId) {
        return quizAggregateRepository.getQuiz(quizId);
    }
    public List<GetQuizDTO> getQuizzesWithPaginationForGivenAdmin(Long adminId, int offset, int limit) {
        return quizAggregateRepository.getQuizzesWithPaginationForGivenAdmin(adminId, offset, limit);
    }
}
