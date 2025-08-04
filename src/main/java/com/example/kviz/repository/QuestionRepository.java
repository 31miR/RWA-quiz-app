package com.example.kviz.repository;

import com.example.kviz.model.Question;
import com.example.kviz.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class QuestionRepository {

    public void save(Question question) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(question);
        em.getTransaction().commit();
        em.close();
    }

    public void update(Question question) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(question);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Question question) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Question merged = em.merge(question);
        em.remove(merged);
        em.getTransaction().commit();
        em.close();
    }

    public Question findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Question question = em.find(Question.class, id);
        em.close();
        return question;
    }

    public List<Question> findByQuizIdOrdered(Long quizId) {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Question> query = em.createQuery(
            "SELECT q FROM Question q WHERE q.quiz.id = :quizId ORDER BY q.orderNumber", Question.class);
        query.setParameter("quizId", quizId);
        List<Question> result = query.getResultList();
        em.close();
        return result;
    }
}
