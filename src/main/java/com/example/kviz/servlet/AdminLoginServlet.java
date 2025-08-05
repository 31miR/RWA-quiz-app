package com.example.kviz.servlet;

import java.io.BufferedReader;
import java.io.IOException;

import com.google.gson.Gson;

import com.example.kviz.service.AdminService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class AdminLoginResponse {
    public boolean isUsernameCorrect;
    public boolean isLoginSuccessful;
    public String errorMessage;
    AdminLoginResponse(boolean isUsernameCorrect, boolean isLoginSuccessful, String errorMessage) {
        this.isUsernameCorrect = isUsernameCorrect;
        this.isLoginSuccessful = isLoginSuccessful;
        this.errorMessage = errorMessage;
    }
}

class LoginRequest {
    public String username;
    public String password;
}

@WebServlet(name = "AdminLoginServlet", urlPatterns = "/api/admin/login")
public class AdminLoginServlet extends HttpServlet {
    public final AdminService adminService = new AdminService();
    public final Gson gson = new Gson();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        BufferedReader reader = request.getReader();
        LoginRequest loginRequest = gson.fromJson(reader, LoginRequest.class);

        String username = loginRequest.username;
        String password = loginRequest.password;

        if (!adminService.doesAdminWithUsernameExist(username)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(new AdminLoginResponse(false,false,"No profile with given username, username: " + username)));
            return;
        }

        if (!adminService.isLoginDataCorrect(username, password)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(new AdminLoginResponse(true, false, "Bad password")));
            return;
        }

        request.getSession().setAttribute("username", username);
        if (adminService.isAdminSuperAdmin(username)) {
            request.getSession().setAttribute("isSuperAdmin", true);
        }
        else {
            request.getSession().setAttribute("isSuperAdmin", false);
        }

        response.getWriter().write(gson.toJson(new AdminLoginResponse(true, true, "No error")));
    }
}
