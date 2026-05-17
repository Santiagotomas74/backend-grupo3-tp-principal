-- TODO: Pasar estos a CSV

-- =========================
-- TIPOS LUGAR OPERATIVO
-- =========================

INSERT INTO Tipo_Lugar_Operativo (id, nombre) VALUES
(1, 'Planta'),
(2, 'Estacion de Servicio'),
(3, 'Deposito');

-- =========================
-- ESTADOS VEHICULO
-- =========================

INSERT INTO Estado_Vehiculo (id, nombre) VALUES
(1, 'Disponible'),
(2, 'En Viaje'),
(3, 'Mantenimiento');

-- =========================
-- TIPOS VINCULO
-- =========================

INSERT INTO Tipo_Vinculo (id, nombre) VALUES
(1, 'Empleado'),
(2, 'Tercerizado');

-- =========================
-- ESTADOS ORDEN
-- =========================

INSERT INTO Estado_Orden_Carga (id, nombre) VALUES
(1, 'Pendiente'),
(2, 'En Curso'),
(3, 'Entregada'),
(4, 'Cancelada');

-- =========================
-- TIPOS INCIDENCIA
-- =========================

INSERT INTO Tipo_Incidencia (id, nombre) VALUES
(1, 'Demora'),
(2, 'Accidente'),
(3, 'Documentacion');

-- =========================
-- TIPOS DOCUMENTO
-- =========================

INSERT INTO Tipo_Documento (id, nombre, categoria) VALUES
(1, 'Licencia Conducir', 'Transportista'),
(2, 'Seguro Vehicular', 'Vehiculo'),
(3, 'VTV', 'Vehiculo');