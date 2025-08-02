package com.example.kviz.listener;

import com.example.kviz.util.JPAUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialize JPA on application startup
        JPAUtil.getEntityManagerFactory();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Close JPA resources on application shutdown
        JPAUtil.shutdown();
    }
}