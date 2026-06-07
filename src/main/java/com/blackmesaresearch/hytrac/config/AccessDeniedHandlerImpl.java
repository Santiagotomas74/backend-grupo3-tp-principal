package com.blackmesaresearch.hytrac.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {

        // Vemos quién intentó mandarse por donde no debía
        String email = request.getUserPrincipal() != null 
            ? request.getUserPrincipal().getName() 
            : "anónimo";
            
        // Alarma en consola (WARN = Advertencia)
        logger.warn("[AUDITORÍA DE SEGURIDAD - 403] Acceso denegado por Rol | Usuario: {} | URL: {} | Método: {}", 
            email, request.getRequestURI(), request.getMethod());
                       
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Para 403
        response.getWriter().write("""
            {"success": false, "message": "Acceso denegado: No tenés los permisos necesarios (Rol incorrecto)."}""");
    }
}