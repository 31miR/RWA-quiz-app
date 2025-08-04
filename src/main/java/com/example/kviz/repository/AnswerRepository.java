package com.example.kviz.repository;

import com.example.kviz.model.Answer;
import com.example.kviz.util.JPAUtil;

import jakarta.persistence.EntityManager;
import java.util.List;

public class AnswerRepository {

    public void save(Answer answer) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(answer);
        em.getTransaction().commit();
        em.close();
    }

    public void update(Answer answer) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(answer);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Answer answer) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Answer merged = em.merge(answer);
        em.remove(merged);
        em.getTransaction().commit();
        em.close();
    }

    public Answer findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Answer answer = em.find(Answer.class, id);
        em.close();
        return answer;
    }

    public List<Answer> findByQuestionId(Long questionId) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Answer> result = em.createQuery(
            "SELECT a FROM Answer a WHERE a.question.id = :questionId", Answer.class)
            .setParameter("questionId", questionId)
            .getResultList();
        em.close();
        return result;
    }
}
