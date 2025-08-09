package com.example.kviz.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.kviz.model.Admin;
import com.example.kviz.service.AdminService;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class AdminDTO {
    public Long id;
    public String fullName;
    public String username;
    public String password;
    public Boolean isSuperAdmin;
}

@WebServlet(name = "AdminCRUDServlet", urlPatterns = {"/api/superadmin/admin/*", "/api/superadmin/admin"})
public class AdminCRUDServlet extends HttpServlet {
    public final AdminService adminService = new AdminService();
    public final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String idParam = request.getParameter("id");
        String path = request.getPathInfo();
        String fullPath = request.getContextPath() + request.getServletPath() + (path == null ? "" : path);
        //this path is expected if the request came for a list of admins
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
            List<Admin> admins = adminService.findWithPagination(offset, limit);
            List<AdminDTO> adminsRAW = new ArrayList<AdminDTO>();
            for (Admin admin : admins) {
                AdminDTO adminRaw = new AdminDTO();
                adminRaw.id = admin.getId();
                adminRaw.fullName = admin.getFullName();
                adminRaw.username = admin.getUsername();
                adminRaw.password = admin.getPassword();
                adminRaw.isSuperAdmin = admin.getIsSuperAdmin();
                adminsRAW.add(adminRaw);
            }
            response.getWriter().write(gson.toJson(adminsRAW));
            return;
        }
        //otherwise the request came for a specific admin
        Long adminId;
        try {
            adminId = Long.parseLong(idParam);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Unexpected url accessed: " + fullPath + "\"}");
            return;
        }
        Admin admin = adminService.findById(adminId);
        if (admin == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Admin with given id does not exist. id given: " + adminId.toString() + "\"}");
            return;
        }
        AdminDTO adminRaw = new AdminDTO();
        adminRaw.id = admin.getId();
        adminRaw.fullName = admin.getFullName();
        adminRaw.username = admin.getUsername();
        adminRaw.password = admin.getPassword();
        adminRaw.isSuperAdmin = admin.getIsSuperAdmin();
        response.getWriter().write(gson.toJson(adminRaw));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        BufferedReader reader = request.getReader();
        AdminDTO adminRaw = gson.fromJson(reader, AdminDTO.class);
        Admin admin = new Admin();
        admin.setFullName(adminRaw.fullName);
        admin.setUsername(adminRaw.username);
        admin.setPassword(adminRaw.password);
        admin.setIsSuperAdmin(adminRaw.isSuperAdmin == null ? false : adminRaw.isSuperAdmin);
        try {
            adminService.registerNewAdmin(admin);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Username already taken\"}");
            return;
        }
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("{\"message\":\"Successfuly created a new admin\"}");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        BufferedReader reader = request.getReader();
        AdminDTO adminRaw = gson.fromJson(reader, AdminDTO.class);
        Admin admin = new Admin();
        Admin existingAdmin;
        if (adminRaw.id != null) {
            admin.setId(adminRaw.id);
        }
        else {
            String idParam = request.getParameter("id");
            Long id;
            try {
                id = Long.parseLong(idParam);
            }
            catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Couldn't obtain admin id for updating\"}");
                return;
            }
            admin.setId(id);
        }
        existingAdmin = adminService.findById(admin.getId());
        if (existingAdmin == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"No admin with given id in the database\"}");
            return;
        }
        admin.setFullName(adminRaw.fullName == null ? existingAdmin.getFullName() : adminRaw.fullName);
        admin.setUsername(adminRaw.username == null ? existingAdmin.getUsername() : adminRaw.username);
        admin.setPassword(adminRaw.password == null ? existingAdmin.getPassword() : adminRaw.password);
        admin.setIsSuperAdmin(adminRaw.isSuperAdmin == null ? existingAdmin.getIsSuperAdmin() : adminRaw.isSuperAdmin);
        try {
            adminService.updateAdmin(admin);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Username already taken\"}");
            return;
        }
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("{\"message\":\"Successfuly updated the admin\"}");
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
        Admin admin = adminService.findById(id);
        if (admin == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Cannot find admin with given id to delete\"}");
            return;
        }
        adminService.delete(admin);
        response.getWriter().write("{\"message\":\"Successfuly deleted the admin\"}");
    }
}
