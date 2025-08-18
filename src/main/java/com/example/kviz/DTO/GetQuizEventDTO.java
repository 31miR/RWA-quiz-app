package com.example.kviz.DTO;

import java.util.List;

import com.example.kviz.model.QuizEvent;
import com.example.kviz.service.QuizEventService;

public class GetQuizEventDTO {
    public Long id;
    public String pin;
    public boolean eventActive;
    public String dateTimeCreated;
    public Long quizId;
    public String quizName;
    public List<QuizPlayerDTO> players;
    public GetQuizEventDTO() {}
    public GetQuizEventDTO(QuizEvent eventEntity) {
        QuizEventService quizEventService = new QuizEventService();
        this.id = eventEntity.getId();
        this.pin = eventEntity.getPin();
        this.eventActive = eventEntity.getEventActive();
        this.dateTimeCreated = eventEntity.getDateTimeCreated().toString();
        this.quizId = eventEntity.getQuiz().getId();
        this.quizName = eventEntity.getQuiz().getTitle();
        this.players = quizEventService.getAllPlayersForQuizEvent(eventEntity.getId());
    }
}
