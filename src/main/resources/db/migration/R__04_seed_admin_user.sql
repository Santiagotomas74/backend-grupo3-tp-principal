INSERT OR IGNORE INTO Usuario (
    id,
    nombre,
    apellido,
    dni,
    email,
    legajo,
    password_hash,
    activo
)

VALUES (
    1,
    'System',
    'Administrator',
    99999999,
    'admin@hytrac.local',
    'ADMIN001',
    -- hasheado con BCrypt. es 'wearehytrac'
    -- https://bcrypt-generator.com/
    '$2a$10$xh9ExMOkrDViNPQX1sdn7eeaHX.Rpdg4Dl.rdoD8DFuCYEQIsHN3i',
    1
);

INSERT OR IGNORE INTO Usuario_Rol (
    usuario_id,
    rol_id
)
SELECT
    u.id,
    r.id
FROM Usuario u
JOIN Rol r
WHERE u.email = 'admin@hytrac.local'
  AND r.nombre = 'ADMIN';