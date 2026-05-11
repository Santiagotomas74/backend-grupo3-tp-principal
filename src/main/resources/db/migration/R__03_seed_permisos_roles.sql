-- Darle acceso irrestricto al rol ADMIN
INSERT OR IGNORE INTO Rol_Permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM Rol r
CROSS JOIN Permiso p
WHERE r.nombre = 'ADMIN';