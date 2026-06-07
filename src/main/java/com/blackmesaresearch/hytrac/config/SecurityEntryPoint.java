package com.blackmesaresearch.hytrac.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(SecurityEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {


        logger.warn("[AUDITORÍA DE SEGURIDAD - 401] Intento de acceso sin autenticación | IP: {} | URL: {} | Método: {}", 
            request.getRemoteAddr(), request.getRequestURI(), request.getMethod());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Para 401
        response.getWriter().write("""
            {"success": false, "message": "Token inválido o no proporcionado."}""");
    }
}
