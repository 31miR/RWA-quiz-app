package com.example.kviz.DTO;

public class QuizPlayerDTO {
    public Long id;
    public String playerName;
    public int score;

    QuizPlayerDTO() {}

    QuizPlayerDTO(Long id, String playerName, int score) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
    }
}
