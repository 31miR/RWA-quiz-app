package com.example.kviz.repository;

import java.util.List;

import com.example.kviz.model.QuizPlayer;
import com.example.kviz.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class QuizPlayerRepository {

    public void addPlayer(QuizPlayer player) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(player);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Update samo score-a
    public void updatePlayerScore(Long id, int newScore) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            QuizPlayer player = em.find(QuizPlayer.class, id);
            if (player != null) {
                player.setScore(newScore);
                em.merge(player);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public QuizPlayer getPlayerById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(QuizPlayer.class, id);
        } finally {
            em.close();
        }
    }

    public List<QuizPlayer> getTop10PlayersForQuizEvent(Long quizEventId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<QuizPlayer> query = em.createQuery(
                "SELECT p FROM QuizPlayer p WHERE p.quizEvent.id = :quizEventId ORDER BY p.score DESC",
                QuizPlayer.class
            );
            query.setParameter("quizEventId", quizEventId);
            query.setMaxResults(10);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<QuizPlayer> getAllPlayersForQuizEvent(Long quizEventId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<QuizPlayer> query = em.createQuery(
                "SELECT p FROM QuizPlayer p WHERE p.quizEvent.id = :quizEventId ORDER BY p.score DESC",
                QuizPlayer.class
            );
            query.setParameter("quizEventId", quizEventId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
