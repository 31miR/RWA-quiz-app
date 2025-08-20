package com.example.kviz.servlet;

import java.io.IOException;
import java.util.List;

import com.example.kviz.DTO.QuizPlayerDTO;
import com.example.kviz.service.QuizEventService;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "QuizPlayerGeter", urlPatterns = "/api/quizPlayer")
public class QuizPlayerGeter extends HttpServlet {
    public final QuizEventService quizEventService = new QuizEventService();
    Gson gson = new Gson();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        String name = request.getParameter("playerName");
        Long quizEventId = Long.parseLong(request.getParameter("quizEventId"));

        List<QuizPlayerDTO> players = quizEventService.getAllPlayersForQuizEvent(quizEventId);
        
        for (QuizPlayerDTO p : players) {
            if (p.playerName.equals(name)) {
                response.getWriter().write(gson.toJson(p));
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write("{}");
    }
}
