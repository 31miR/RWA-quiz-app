package com.example.kviz.repository;

import com.example.kviz.model.Admin;
import com.example.kviz.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class AdminRepository {

    public void save(Admin admin) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(admin);
        em.getTransaction().commit();
        em.close();
    }

    public void update(Admin admin) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(admin);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Admin admin) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Admin merged = em.merge(admin);
        em.remove(merged);
        em.getTransaction().commit();
        em.close();
    }

    public Admin findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Admin admin = em.find(Admin.class, id);
        em.close();
        return admin;
    }

    public List<Admin> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Admin> result = em.createQuery("SELECT a FROM Admin a", Admin.class).getResultList();
        em.close();
        return result;
    }

    public Admin findByUsername(String username) {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Admin> query = em.createQuery("SELECT a FROM Admin a WHERE a.username = :username", Admin.class);
        query.setParameter("username", username);
        List<Admin> result = query.getResultList();
        em.close();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Admin> findWithPagination(int offset, int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Admin> query = em.createQuery("SELECT a FROM Admin a ORDER BY a.id", Admin.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        List<Admin> result = query.getResultList();
        em.close();
        return result;
    }
}
