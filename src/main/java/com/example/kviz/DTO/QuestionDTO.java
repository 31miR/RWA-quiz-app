package com.example.kviz.DTO;

import java.util.List;

public class QuestionDTO {
    public Long id;
    public String questionText;
    public int timeInterval;
    public int pointAmmount;
    public int questionPosition;
    public Long quizId;
    public List<AnswerDTO> answers;
}
