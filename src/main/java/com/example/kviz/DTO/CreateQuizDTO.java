package com.example.kviz.DTO;

import java.util.List;

public class CreateQuizDTO {
    public Long id;
    public String title;
    public String description;
    public boolean isImageSent; //this one should be true if creating, but if updating it can be false
    public Long adminId;
    public List <QuestionDTO> questions;
}
