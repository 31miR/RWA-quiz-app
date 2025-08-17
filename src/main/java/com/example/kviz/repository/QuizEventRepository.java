package com.example.kviz.repository;

import java.util.List;

import com.example.kviz.model.QuizEvent;
import com.example.kviz.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class QuizEventRepository {

    public void add(QuizEvent quizEvent) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(quizEvent);
            em.getTransaction().commit();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public QuizEvent findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(QuizEvent.class, id);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<QuizEvent> findWithPagination(int offset, int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<QuizEvent> query = em.createQuery(
                "SELECT qe FROM QuizEvent qe ORDER BY qe.dateTimeCreated DESC", 
                QuizEvent.class
            );
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<QuizEvent> getAllActiveEvents() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<QuizEvent> query = em.createQuery(
                "SELECT qe FROM QuizEvent qe WHERE qe.eventActive = :isActive", 
                QuizEvent.class
            );
            query.setParameter("isActive", true);
            return query.getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public boolean isPINinUse(String pin) {
    EntityManager em = JPAUtil.getEntityManager();
    try {
            TypedQuery<QuizEvent> query = em.createQuery(
                "SELECT e FROM QuizEvent e WHERE e.pin = :pin AND e.eventActive = true",
                QuizEvent.class
            );
            query.setParameter("pin", pin);
            query.setMaxResults(1);

            return !query.getResultList().isEmpty();
        } finally {
            em.close();
        }
    }

    public void swapEventActive(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            QuizEvent quizEvent = em.find(QuizEvent.class, id);
            if (quizEvent != null) {
                quizEvent.setEventActive(!quizEvent.isEventActive());
            }
            em.getTransaction().commit();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}