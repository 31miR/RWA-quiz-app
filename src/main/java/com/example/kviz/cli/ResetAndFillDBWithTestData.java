package com.example.kviz.cli;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.example.kviz.DTO.CreateQuizDTO;
import com.example.kviz.model.Admin;
import com.example.kviz.service.AdminService;
import com.example.kviz.service.QuizCRUDService;
import com.example.kviz.util.JPAUtil;
import com.google.gson.Gson;

import jakarta.persistence.EntityManager;

public class ResetAndFillDBWithTestData {

    public static final AdminService adminService = new AdminService();
    public static final QuizCRUDService quizCRUDService = new QuizCRUDService();
    public static final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        clearDatabase();

        Admin superAdmin = new Admin();
        superAdmin.setFullName("Super admin");
        superAdmin.setIsSuperAdmin(true);
        superAdmin.setUsername("suad");
        superAdmin.setPassword("superadmin");

        Admin regularAdmin = new Admin();
        regularAdmin.setFullName("Regular admin");
        regularAdmin.setIsSuperAdmin(false);
        regularAdmin.setUsername("read");
        regularAdmin.setPassword("regularadmin");

        adminService.save(superAdmin);
        adminService.save(regularAdmin);

        String pathToQuiz1 = "src/main/resources/quiz1.json";
        String pathToQuiz2 = "src/main/resources/quiz2.json";
        
        if (!Files.exists(Paths.get(pathToQuiz1))) {
            System.err.println("JSON file not found: " + pathToQuiz1);
            return;
        }

        
        if (!Files.exists(Paths.get(pathToQuiz2))) {
            System.err.println("JSON file not found: " + pathToQuiz2);
            return;
        }

        CreateQuizDTO quiz1 = gson.fromJson(new FileReader(pathToQuiz1), CreateQuizDTO.class);
        CreateQuizDTO quiz2 = gson.fromJson(new FileReader(pathToQuiz2), CreateQuizDTO.class);

        quiz1.adminId = regularAdmin.getId();
        quiz2.adminId = regularAdmin.getId();

        quizCRUDService.createQuiz(quiz1);
        quizCRUDService.createQuiz(quiz2);

        System.out.println("Database filled with test data.");
    }

    private static void clearDatabase() {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        em.createQuery("DELETE FROM QuizPlayer").executeUpdate();
        em.createQuery("DELETE FROM QuizEvent").executeUpdate();
        em.createQuery("DELETE FROM Answer").executeUpdate();
        em.createQuery("DELETE FROM Question").executeUpdate();
        em.createQuery("DELETE FROM Quiz").executeUpdate();
        em.createQuery("DELETE FROM Admin").executeUpdate();

        em.getTransaction().commit();
        em.close();
    }
}
