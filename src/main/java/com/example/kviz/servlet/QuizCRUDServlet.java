package com.example.kviz.servlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.example.kviz.DTO.CreateQuizDTO;
import com.example.kviz.DTO.GetQuizDTO;
import com.example.kviz.service.QuizCRUDService;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@MultipartConfig
@WebServlet(name = "QuizCRUDServlet", urlPatterns = {"/api/admin/quiz/*", "/api/admin/quiz"})
public class QuizCRUDServlet extends HttpServlet {
    public final QuizCRUDService quizCRUDService = new QuizCRUDService();
    public final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String idParam = request.getParameter("id");
        String path = request.getPathInfo();
        String fullPath = request.getContextPath() + request.getServletPath() + (path == null ? "" : path);
        if (idParam == null) {
            int offset = 0;
            int limit = 10;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
                limit = Integer.parseInt(request.getParameter("limit"));
            }
            catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Unexpected url accessed: " + fullPath + "\"}");
                return;
            }
            Long adminId = (Long) request.getSession().getAttribute("adminId");
            List<GetQuizDTO> quizzes = quizCRUDService.getQuizzesWithPaginationForGivenAdmin(adminId, offset, limit);
            response.getWriter().write(gson.toJson(quizzes));
            return;
        }
        Long quizId;
        try {
            quizId = Long.parseLong(idParam);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Unexpected url accessed: " + fullPath + "\"}");
            return;
        }
        GetQuizDTO quiz = quizCRUDService.getQuizById(quizId);
        response.getWriter().write(gson.toJson(quiz));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        Part quizPart = request.getPart("quiz");
        if (quizPart == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Missing quiz JSON data\"}");
            return;
        }

        // Parse JSON into DTO
        CreateQuizDTO quiz = gson.fromJson(new java.io.InputStreamReader(quizPart.getInputStream()), CreateQuizDTO.class);

        // Handle image if provided
        Part imagePart = request.getPart("image");
        if (imagePart != null && imagePart.getSize() > 0) {
            String imagePath = saveImage(imagePart);
            if (imagePath == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Failed to save image\"}");
                return;
            }
            quiz.imageURI = imagePath;
        }

        HttpSession session = request.getSession(false);
        Long adminId = (Long) session.getAttribute("adminId");
        quiz.adminId = adminId;

        quizCRUDService.createQuiz(quiz);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("{\"message\":\"Successfully created a new quiz\"}");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        String oldImagePath = null; //If the new image is sent, we should delete the old one, but only after everything is done

        Part quizPart = request.getPart("quiz");
        if (quizPart == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Missing quiz JSON data\"}");
            return;
        }

        CreateQuizDTO quiz = gson.fromJson(new java.io.InputStreamReader(quizPart.getInputStream()), CreateQuizDTO.class);

        GetQuizDTO existingQuiz = quizCRUDService.getQuizById(quiz.id);
        if (existingQuiz == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"No quiz with given id in the database\"}");
            return;
        }

        if (quiz.isImageSent) { // boolean polje u DTO-u
            Part imagePart = request.getPart("image");
            if (imagePart != null && imagePart.getSize() > 0) {
                String imagePath = saveImage(imagePart);
                if (imagePath == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\":\"Failed to save image\"}");
                    return;
                }
                oldImagePath = existingQuiz.imageURI;
                quiz.imageURI = imagePath;
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Image change requested but no file provided\"}");
                return;
            }
        } else {
            quiz.imageURI = existingQuiz.imageURI;
        }

        HttpSession session = request.getSession(false);
        Long adminId = (Long) session.getAttribute("adminId");
        quiz.adminId = adminId;

        quizCRUDService.updateQuiz(quiz);
        if (quiz.isImageSent && oldImagePath != null) {
            deleteImageFile(oldImagePath);
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\":\"Successfully updated the quiz\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        Long id;
        try {
            id = Long.parseLong(idParam);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Cannot read id\"}");
            return;
        }
        GetQuizDTO quiz = quizCRUDService.getQuizById(id);
        if (quiz == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Cannot find quiz with given id to delete\"}");
            return;
        }
        String imgPath = quizCRUDService.getQuizById(id).imageURI;
        quizCRUDService.deleteQuiz(id);
        deleteImageFile(imgPath);
        response.getWriter().write("{\"message\":\"Successfuly deleted the quiz\"}");
    }

    private String saveImage(Part imagePart) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + imagePart.getSubmittedFileName();
        String uploadPath = getServletContext().getRealPath("") + "uploads";
        java.io.File uploadDir = new java.io.File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();
        java.io.File file = new java.io.File(uploadDir, fileName);
        try (java.io.InputStream input = imagePart.getInputStream()) {
            java.nio.file.Files.copy(input, file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        return "uploads/" + fileName;
    }

    private void deleteImageFile(String imagePath) {
    if (imagePath != null && !imagePath.isEmpty()) {
        try {
            String absolutePath = getServletContext().getRealPath("") + imagePath;
            Path path = Paths.get(absolutePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace(); // ili log
        }
    }
}
}
