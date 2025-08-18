package com.example.kviz.servlet;

import java.io.IOException;

import com.example.kviz.model.QuizEvent;
import com.example.kviz.service.QuizEventService;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class ResponseType {
    public String quizEventId;
}

@WebServlet(name = "QuizEventGetFromPinServlet", urlPatterns = "/api/quizEvent")
public class QuizEventGetFromPinServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final QuizEventService quizEventService = new QuizEventService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String pin = request.getParameter("pin");
        QuizEvent quizEvent = quizEventService.getActiveQuizByPIN(pin);
        if (quizEvent == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ResponseType resp = new ResponseType();
            resp.quizEventId = null;
            response.getWriter().write(gson.toJson(resp));
            return;
        }
        ResponseType resp = new ResponseType();
        resp.quizEventId = quizEvent.getPin();
        response.getWriter().write(gson.toJson(resp));
    }
}
