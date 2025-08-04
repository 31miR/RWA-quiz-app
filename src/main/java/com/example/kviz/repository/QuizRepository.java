package com.example.kviz.repository;

import com.example.kviz.model.Quiz;
import com.example.kviz.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class QuizRepository {

    public void save(Quiz quiz) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(quiz);
        em.getTransaction().commit();
        em.close();
    }

    public void update(Quiz quiz) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(quiz);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Quiz quiz) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Quiz merged = em.merge(quiz);
        em.remove(merged);
        em.getTransaction().commit();
        em.close();
    }

    public Quiz findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Quiz quiz = em.find(Quiz.class, id);
        em.close();
        return quiz;
    }

    public List<Quiz> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Quiz> result = em.createQuery("SELECT q FROM Quiz q", Quiz.class).getResultList();
        em.close();
        return result;
    }

    public List<Quiz> findByAdminId(Long adminId) {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Quiz> query = em.createQuery("SELECT q FROM Quiz q WHERE q.admin.id = :adminId", Quiz.class);
        query.setParameter("adminId", adminId);
        List<Quiz> result = query.getResultList();
        em.close();
        return result;
    }

    public List<Quiz> findWithPagination(int offset, int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Quiz> query = em.createQuery("SELECT q FROM Quiz q ORDER BY q.id", Quiz.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        List<Quiz> result = query.getResultList();
        em.close();
        return result;
    }
}
