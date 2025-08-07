package com.example.kviz.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);

        String path = request.getRequestURI();

        if (path.equals("/kviz/admin/login") || path.equals("/kviz/admin/logout")) {
            chain.doFilter(request, response);
            return;
        }

        boolean isLoggedIn = session != null && session.getAttribute("adminId") != null;

        if (!isLoggedIn) {
            response.sendRedirect("/login");
            return;
        }

        chain.doFilter(req, res);
    }
}
