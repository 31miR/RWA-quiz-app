package com.example.kviz.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class QuizEvent {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    boolean eventActive;
    @Column(nullable=false)
    OffsetDateTime dateTimeCreated;
    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;
    @OneToMany(mappedBy = "quizEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizPlayer> players = new ArrayList<>();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEventActive() {
        return this.eventActive;
    }

    public boolean getEventActive() {
        return this.eventActive;
    }

    public void setEventActive(boolean eventActive) {
        this.eventActive = eventActive;
    }

    public OffsetDateTime getDateTimeCreated() {
        return this.dateTimeCreated;
    }

    public void setDateTimeCreated(OffsetDateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public Quiz getQuiz() {
        return this.quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<QuizPlayer> getPlayers() {
        return this.players;
    }

    public void setPlayers(List<QuizPlayer> players) {
        this.players = players;
    }
}
