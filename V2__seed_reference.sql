-- =========================
-- PROVINCIAS
-- =========================

INSERT INTO Provincia (id, nombre) VALUES
(1, 'Buenos Aires'),
(2, 'Cordoba'),
(3, 'Santa Fe');

-- =========================
-- LOCALIDADES
-- =========================

INSERT INTO Localidad (id, provincia_id, nombre, codigo_postal) VALUES
(1, 1, 'San Miguel', '1663'),
(2, 1, 'Moreno', '1744'),
(3, 2, 'Cordoba Capital', '5000');

-- =========================
-- COMBUSTIBLES
-- =========================

INSERT INTO Combustible (
    id,
    nombre,
    numero_onu,
    clase_riesgo,
    densidad,
    temperatura_referencia
) VALUES
(1, 'Nafta Super', '1203', '3', 0.7450, 15.0),
(2, 'Diesel', '1202', '3', 0.8320, 15.0);

