package com.example.kviz.DTO;

import java.util.List;

public class GetQuizDTO {
    public Long id;
    public String title;
    public String description;
    public String imageURI;
    public Long adminId;
    public List <QuestionDTO> questions;
}
