package com.blackmesaresearch.hytrac.config;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
                       
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Para 403
        response.getWriter().write("""
            {"success": false, "message": "Acceso denegado: No tenés los permisos necesarios (Rol incorrecto)."}
        """);
    }
}