package com.blackmesaresearch.hytrac.service;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.model.core.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

    private static final String SECRET =
        "mi_clave_super_secreta_hytrac_2026";

    private final Key key = new SecretKeySpec(
        SECRET.getBytes(),
        SignatureAlgorithm.HS256.getJcaName()
    );

    public String generarToken(Usuario usuario) {


        // =========================
        // JWT
        // =========================

        return Jwts.builder()

            .setSubject(usuario.getEmail())

            .claim("id", usuario.getId())
            .claim("nombre", usuario.getNombre())
            .claim("apellido", usuario.getApellido())
            .claim("rol", usuario.getRol().getNombre())

            .setIssuedAt(new Date())

            .setExpiration(
                new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)
            )

            .signWith(key)

            .compact();
    }
}