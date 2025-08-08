package com.example.kviz.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/superadmin/*"})
public class SuperAdminAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);

        boolean isLoggedIn = session != null && session.getAttribute("adminId") != null;

        boolean isSuperAdmin;
        if (session == null) {
            isSuperAdmin = false;
        } else if (session.getAttribute("isSuperAdmin") == null) {
            isSuperAdmin = false;
        } else {
            isSuperAdmin = (Boolean) session.getAttribute("isSuperAdmin");
        }

        if (!isLoggedIn) {
            response.sendRedirect("/kviz/admin/login");
            return;
        }

        if (!isSuperAdmin) {
            response.sendRedirect("/kviz/admin/login");
            return;
        }

        chain.doFilter(req, res);
    }
}
