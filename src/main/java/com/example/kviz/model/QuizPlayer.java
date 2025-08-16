package com.example.kviz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class QuizPlayer {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "quiz_event_id", nullable = false)
    private QuizEvent quizEvent;
    @Column(nullable=false)
    private String playerName;
    @Column(nullable=false)
    private int score;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuizEvent getQuizEvent() {
        return this.quizEvent;
    }

    public void setQuizEvent(QuizEvent quizEvent) {
        this.quizEvent = quizEvent;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
