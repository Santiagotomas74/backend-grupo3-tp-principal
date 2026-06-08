CREATE TABLE notificacion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    legajo_receptor TEXT NOT NULL,
    descripcion TEXT NOT NULL,
    visto BOOLEAN DEFAULT 0,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);