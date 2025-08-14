package com.example.kviz.repository;

import com.example.kviz.DTO.*;
import com.example.kviz.model.Admin;
import com.example.kviz.model.Answer;
import com.example.kviz.model.Question;
import com.example.kviz.model.Quiz;
import com.example.kviz.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class QuizAggregateRepository {
    //need these for the helper function at the end
    QuestionRepository questionRepository = new QuestionRepository();
    AnswerRepository answerRepository = new AnswerRepository();

    public void createQuiz(CreateQuizDTO quizRaw) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        Quiz newQuiz = new Quiz();
        
        newQuiz.setTitle(quizRaw.title);
        newQuiz.setDescription(quizRaw.description);
        newQuiz.setImageURI(quizRaw.imageURI);
        newQuiz.setQuestions(new ArrayList<Question>());
        
        Admin admin = em.find(Admin.class, quizRaw.adminId);
        if (admin == null) {
            throw new IllegalArgumentException("Could not find the admin that is supposedly creating the quiz in the database!");
        }
        newQuiz.setAdmin(admin);
        
        for (QuestionDTO questionRaw : quizRaw.questions) {
            Question question = new Question();
            question.setQuiz(newQuiz);
            question.setQuestionText(questionRaw.questionText);
            question.setPointAmount(questionRaw.pointAmmount);
            question.setQuestionPosition(questionRaw.questionPosition);
            question.setTimeInterval(questionRaw.timeInterval);
            question.setAnswers(new ArrayList<Answer>());
            for (AnswerDTO answerRaw : questionRaw.answers) {
                Answer answer = new Answer();
                answer.setIsRight(answerRaw.isRight);
                answer.setQuestion(question);
                answer.setText(answerRaw.text);
                question.getAnswers().add(answer);
            }
            newQuiz.getQuestions().add(question);
        }

        em.persist(newQuiz);
        em.getTransaction().commit();
        em.close();
    }
    public void updateQuiz(CreateQuizDTO quizRaw) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        Quiz quiz = em.find(Quiz.class, quizRaw.id);
        if (quiz == null) {
            throw new IllegalArgumentException("Could not find the quiz in the database to update!");
        }
        
        quiz.setTitle(quizRaw.title);
        quiz.setDescription(quizRaw.description);
        if (quizRaw.isImageSent) {
            quiz.setImageURI(quizRaw.imageURI);
        }
        quiz.getQuestions().clear();
        
        for (QuestionDTO questionRaw : quizRaw.questions) {
            Question question = new Question();
            question.setQuiz(quiz);
            question.setQuestionText(questionRaw.questionText);
            question.setPointAmount(questionRaw.pointAmmount);
            question.setQuestionPosition(questionRaw.questionPosition);
            question.setTimeInterval(questionRaw.timeInterval);
            question.setAnswers(new ArrayList<Answer>());
            for (AnswerDTO answerRaw : questionRaw.answers) {
                Answer answer = new Answer();
                answer.setIsRight(answerRaw.isRight);
                answer.setQuestion(question);
                answer.setText(answerRaw.text);
                question.getAnswers().add(answer);
            }
            quiz.getQuestions().add(question);
        }

        em.getTransaction().commit();
        em.close();
    }
    public GetQuizDTO getQuiz(Long id) {
        Quiz quiz;
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        quiz = em.find(Quiz.class, id);
        if (quiz == null) {
            throw new IllegalArgumentException("Could not find the Quiz with the given id!");
        }
        em.getTransaction().commit();
        em.close();
        return convertQuizEntityToGetDTO(quiz);
    }
    public List<GetQuizDTO> getQuizzesWithPaginationForGivenAdmin(Long adminId, int offset, int limit) {
        List<Quiz> quizzes;
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        TypedQuery<Quiz> query = em.createQuery("SELECT q FROM Quiz q WHERE q.admin.id = :adminId ORDER BY q.id", Quiz.class);
        query.setParameter("adminId", adminId);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        quizzes = query.getResultList();
        em.getTransaction().commit();
        em.close();
        List<GetQuizDTO> ret = new ArrayList<>();
        for (Quiz quiz : quizzes) {
            ret.add(convertQuizEntityToGetDTO(quiz));
        }
        return ret;
    }
    public void deleteQuiz(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        Quiz quiz = em.find(Quiz.class, id);
        if (quiz != null) {
            em.remove(quiz);
        }

        em.getTransaction().commit();
        em.close();
    }

    //TODO: This requires massive optimizations
    //It should be done with just one query, not looking for every question's answers again and again and again...
    private GetQuizDTO convertQuizEntityToGetDTO(Quiz quiz) {
        GetQuizDTO quizRaw = new GetQuizDTO();
        quizRaw.adminId = quiz.getAdmin().getId();
        quizRaw.description = quiz.getDescription();
        quizRaw.id = quiz.getId();
        quizRaw.imageURI = quiz.getImageURI();
        quizRaw.title = quiz.getTitle();
        quizRaw.questions = new ArrayList<QuestionDTO>();
        List<Question> quizQuestions = questionRepository.findByQuizIdOrdered(quiz.getId());
        for (Question quizQuestion : quizQuestions) {
            QuestionDTO questionRaw = new QuestionDTO();
            questionRaw.id = quizQuestion.getId();
            questionRaw.pointAmmount = quizQuestion.getPointAmount();
            questionRaw.questionPosition = quizQuestion.getQuestionPosition();
            questionRaw.questionText = quizQuestion.getQuestionText();
            questionRaw.timeInterval = quizQuestion.getTimeInterval();
            questionRaw.quizId = quizQuestion.getQuiz().getId();
            questionRaw.answers = new ArrayList<AnswerDTO>();

            List<Answer> questionAnswers = answerRepository.findByQuestionId(quizQuestion.getId());
            for (Answer questionAnswer : questionAnswers) {
                AnswerDTO answerRaw = new AnswerDTO();
                answerRaw.id = questionAnswer.getId();
                answerRaw.isRight = questionAnswer.getIsRight();
                answerRaw.text = questionAnswer.getText();
                answerRaw.questionId = questionAnswer.getQuestion().getId();
                questionRaw.answers.add(answerRaw);
            }

            quizRaw.questions.add(questionRaw);
        }
        return quizRaw;
    }
}
