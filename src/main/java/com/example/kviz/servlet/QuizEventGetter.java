package com.example.kviz.servlet;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.example.kviz.DTO.GetQuizEventDTO;
import com.example.kviz.model.QuizEvent;
import com.example.kviz.service.QuizEventService;
import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class ErrorMessage {
    public String errorMessage;
}

@WebServlet(name = "QuizEventGetter", urlPatterns = "/api/quizEventRaw")
public class QuizEventGetter extends HttpServlet {
    Gson gson = new Gson();
    QuizEventService quizEventService = new QuizEventService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServerException {
        response.setContentType("application/json");

        String offsetString = request.getParameter("offset");
        String limitString = request.getParameter("limit");
        String idString = request.getParameter("id");
        String getXls = request.getParameter("getXls");

        if (idString != null && getXls != null && getXls.equals("true")) {
            respondWithXls(response, idString);
            return;
        }
        if (idString != null) {
            respondWithOneJSON(response, idString);
            return;
        }
        if (offsetString != null && limitString != null) {
            respondWithList(response, offsetString, limitString);
            return;
        }

        respondWithErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid query parameters sent.");
    }

    private void respondWithXls(HttpServletResponse response, String idString) throws IOException {
        Long id = safeConvertStringToLong(idString);
        if (id == null) {
            respondWithErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "quizEvent id must be a number");
        }
        QuizEvent entity = quizEventService.getEventById(id);
        if (entity == null) {
            respondWithErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "could not find that event");
        }
        GetQuizEventDTO quizEventDTO = new GetQuizEventDTO(entity);
        Workbook xlsData = quizEventService.generateXLSWorkbookFromGetQuizEventDAO(quizEventDTO);
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=results.xlsx");

        xlsData.write(response.getOutputStream());
        xlsData.close();
    }

    private void respondWithOneJSON(HttpServletResponse response, String idString) throws IOException {
        Long id = safeConvertStringToLong(idString);
        if (id == null) {
            respondWithErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "quizEvent id must be a number");
        }
        QuizEvent entity = quizEventService.getEventById(id);
        if (entity == null) {
            respondWithErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "could not find that event");
        }
        GetQuizEventDTO quizEventDTO = new GetQuizEventDTO(entity);
        response.getWriter().write(gson.toJson(quizEventDTO));
    }

    private void respondWithList(HttpServletResponse response, String offsetString, String limitString) throws IOException {
        Long offset = safeConvertStringToLong(offsetString);
        if (offset == null) {
            respondWithErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "offset must be a number");
        }
        Long limit = safeConvertStringToLong(limitString);
        if (limit == null) {
            respondWithErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "limit must be a number");
        }
        List<QuizEvent> entities = quizEventService.findQuizEventsWithPagination((int)offset.longValue(), (int)limit.longValue());
        List<GetQuizEventDTO> quizEventDTOs = new ArrayList<>();
        for (QuizEvent entity : entities) {
            quizEventDTOs.add(new GetQuizEventDTO(entity));
        }
        response.getWriter().write(gson.toJson(quizEventDTOs));
    }

    private Long safeConvertStringToLong(String s) {
        try {
            return Long.parseLong(s);
        }
        catch (Exception e) {
            return null;
        }
    }

    private void respondWithErrorMessage(HttpServletResponse response, int scCode, String message) throws IOException {
        response.setStatus(scCode);
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.errorMessage = message;
        response.getWriter().write(gson.toJson(errorMessage));
    }
}
