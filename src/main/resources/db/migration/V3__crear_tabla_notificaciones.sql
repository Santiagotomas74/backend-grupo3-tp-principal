CREATE TABLE notificacion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    legajo_receptor TEXT NOT NULL,
    descripcion TEXT NOT NULL,
    tipo_notificacion TEXT NOT NULL,
    visto BOOLEAN DEFAULT 0,
    enlace TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);