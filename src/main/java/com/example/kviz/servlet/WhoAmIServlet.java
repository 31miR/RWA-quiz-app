package com.example.kviz.servlet;

import java.io.IOException;

import com.example.kviz.model.Admin;
import com.example.kviz.service.AdminService;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class AdminRawData {
    public long id;
    public String fullName;
    public String username;
    public boolean isSuperAdmin;
}

@WebServlet(name = "WhoAmIServlet", urlPatterns = "/api/whoami")
public class WhoAmIServlet extends HttpServlet {
    public final AdminService adminService = new AdminService();
    public final Gson gson = new Gson();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"No session found\"}");
            return;
        }
        
        if (session.getAttribute("adminId") == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"No admin id found in session\"}");
            return;
        }

        Admin admin = adminService.findById((Long) session.getAttribute("adminId"));

        if (admin == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"No admin with given id found in database\"}");
            return;
        }

        AdminRawData adminRaw = new AdminRawData();
        adminRaw.id = admin.getId();
        adminRaw.fullName = admin.getFullName();
        adminRaw.username = admin.getUsername();
        adminRaw.isSuperAdmin = admin.getIsSuperAdmin();

        response.getWriter().write(gson.toJson(adminRaw));
    }
}
