package com.example.kviz.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/api/admin/*"})
public class ApiAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();

        if (path.equals("/kviz/api/admin/login") || path.equals("/kviz/api/admin/logout")) {
            chain.doFilter(request, response);
            return;
        }
        
        HttpSession session = request.getSession(false);

        boolean isLoggedIn = session != null && session.getAttribute("adminId") != null;

        if (!isLoggedIn) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"You need to be an admin to see this\"}");
            return;
        }

        chain.doFilter(req, res);
    }
}
