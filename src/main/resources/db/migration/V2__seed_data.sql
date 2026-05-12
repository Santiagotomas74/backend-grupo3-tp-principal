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

-- =========================
-- ROLES
-- =========================

INSERT INTO Rol (id, nombre, descripcion) VALUES
(1, 'ADMIN', 'Administrador del sistema'),
(2, 'OPERADOR', 'Operador logistico'),
(3, 'TRANSPORTISTA', 'Chofer transportista');

-- =========================
-- PERMISOS
-- =========================

INSERT INTO Permiso (id, codigo, descripcion) VALUES
(1, 'ORDEN_CREAR', 'Crear ordenes'),
(2, 'ORDEN_VER', 'Ver ordenes'),
(3, 'ORDEN_EDITAR', 'Editar ordenes'),
(4, 'USUARIO_ADMIN', 'Administrar usuarios');

-- =========================
-- ROL PERMISOS
-- =========================

INSERT INTO Rol_Permiso (rol_id, permiso_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(2, 1),
(2, 2),
(2, 3),
(3, 2);

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

-- =========================
-- LUGARES OPERATIVOS
-- =========================

INSERT INTO Lugar_Operativo (
    id,
    tipo_id,
    nombre,
    direccion,
    localidad_id,
    latitud,
    longitud,
    puede_recibir,
    puede_despachar,
    activo,
    created_at,
    updated_at
) VALUES
(
    1,
    1,
    'Planta San Miguel',
    'Av Balbin 123',
    1,
    -34.5432,
    -58.7123,
    1,
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    2,
    2,
    'YPF Moreno',
    'Ruta 7 KM 35',
    2,
    -34.6345,
    -58.7987,
    1,
    0,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- =========================
-- USUARIOS
-- =========================

INSERT INTO Usuario (
    id,
    nombre,
    apellido,
    dni,
    email,
    legajo,
    password_hash,
    activo,
    lugar_operativo_id,
    created_at,
    updated_at
) VALUES
(
    1,
    'Santiago',
    'Taher',
    40111222,
    'admin@hytrac.com',
    'LEG001',
    -- password hasheado con BCrypt. es 'wearehytrac'
    '$2a$10$stXoA.WRjdOH3tDxPBkiKOtfIcWs0r/7iMNFEBleKJhXPoY5sozm.',
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    2,
    'Juan',
    'Perez',
    38999111,
    'operador@hytrac.com',
    'LEG002',
    '$2a$10$stXoA.WRjdOH3tDxPBkiKOtfIcWs0r/7iMNFEBleKJhXPoY5sozm.',
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    3,
    'Martin',
    'Gomez',
    35666777,
    'martin@hytrac.com',
    'LEG003',
    '$2a$10$stXoA.WRjdOH3tDxPBkiKOtfIcWs0r/7iMNFEBleKJhXPoY5sozm.',
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    4,
    'Lucia',
    'Fernandez',
    37888999,
    'lucia@hytrac.com',
    'LEG004',
    '$2a$10$stXoA.WRjdOH3tDxPBkiKOtfIcWs0r/7iMNFEBleKJhXPoY5sozm.',
    1,
    2,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    5,
    'Carlos',
    'Ramirez',
    33444555,
    'carlos@hytrac.com',
    'LEG005',
    '$2a$10$stXoA.WRjdOH3tDxPBkiKOtfIcWs0r/7iMNFEBleKJhXPoY5sozm.',
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- =========================
-- USUARIO ROL
-- =========================

INSERT INTO Usuario_Rol (usuario_id, rol_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 3),
(5, 3);

-- =========================
-- EMPRESAS
-- =========================

INSERT INTO Empresa_Tercerizada (
    id,
    nombre_fantasia,
    razon_social,
    cuit,
    direccion,
    localidad_id,
    telefono,
    activo,
    created_at,
    updated_at
) VALUES
(
    1,
    'Transportes Ruta',
    'Transportes Ruta SA',
    '30-12345678-9',
    'Av Central 123',
    1,
    '1122334455',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- =========================
-- TRANSPORTISTAS
-- =========================

INSERT INTO Transportista (
    id,
    usuario_id,
    tipo_vinculo_id,
    cuit,
    empresa_id,
    activo,
    created_at,
    updated_at
) VALUES
(
    1,
    2,
    2,
    '20-44556677-8',
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    2,
    3,
    2,
    '20-33445566-7',
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    3,
    4,
    2,
    '20-33496366-7',
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    4,
    5,
    1,
    '20-99887766-5',
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- =========================
-- CAmiones
-- =========================

INSERT INTO Vehiculo (
    id,
    patente,
    empresa_id,
    peso_maximo_admitido,
    marca,
    modelo,
    estado_id,
    created_at,
    updated_at
) VALUES
(
    1,
    'AA123BB',
    1,
    35000,
    'Scania',
    'R450',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    2,
    'AB456CD',
    1,
    32000,
    'Mercedes-Benz',
    'Actros',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    3,
    'AC789EF',
    1,
    40000,
    'Volvo',
    'FH',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    4,
    'AD741GH',
    1,
    28000,
    'Iveco',
    'Stralis',
    2,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);


-- =========================
-- ACOPLADOS
-- =========================

INSERT INTO Acoplado (
    id,
    patente,
    capacidad_maxima_litros,
    empresa_id,
    estado_id,
    created_at,
    updated_at
) VALUES
(
    1,
    'AC987ZX',
    45000,
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    2,
    'ZX123TY',
    42000,
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    3,
    'QW456ER',
    38000,
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    4,
    'PL741MN',
    50000,
    1,
    2,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- =========================
-- ORDENES
-- =========================

INSERT INTO Orden_Carga (
    id,
    numero_remito,
    cot,
    camion_id,
    acoplado_id,
    transportista_id,
    planta_despacho_id,
    estacion_destino_id,
    operador_id,
    combustible_id,
    estado_id,
    fecha_creacion,
    fecha_salida_planta,
    fecha_entrega_estimada,
    temperatura_carga,
    densidad_carga,
    litros_cargados,
    litros_entregados,
    fie_adjunta,
    observaciones,
    confirmado
) VALUES
(
    1,
    'REM-0001',
    'COT-0001',
    1,
    1,
    1,
    1,
    2,
    1,
    2,
    2,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    DATETIME('now', '+1 day'),
    15.0,
    0.8320,
    30000,
    0,
    1,
    'Carga programada correctamente',
    TRUE
),
(
    2,
    'REM-0002',
    'COT-0002',
    2,
    2,
    2,
    1,
    2,
    1,
    1,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    DATETIME('now', '+2 day'),
    16.5,
    0.8450,
    28000,
    0,
    0,
    'Orden pendiente de despacho',
    TRUE
),
(
    3,
    'REM-0003',
    'COT-0003',
    3,
    3,
    3,
    1,
    2,
    1,
    2,
    2,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    DATETIME('now', '+1 day'),
    14.2,
    0.8300,
    35000,
    12000,
    1,
    'Entrega parcial realizada',
    FALSE
),
(
    4,
    'REM-0004',
    'COT-0004',
    4,
    4,
    2,
    2,
    1,
    1,
    1,
    3,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    DATETIME('now', '+3 day'),
    17.8,
    0.8500,
    25000,
    25000,
    1,
    'Entrega completada correctamente',
    FALSE
);

-- =========================
-- DOCUMENTACION
-- =========================

INSERT INTO Documentacion (
    id,
    tipo_documento_id,
    camion_id,
    nro_documento,
    fecha_emision,
    fecha_vencimiento,
    archivo_url,
    estado_verificacion,
    comentarios
) VALUES
(
    1,
    2,
    1,
    'POLIZA-001',
    DATE('now'),
    DATE('now', '+1 year'),
    '/docs/poliza.pdf',
    1,
    'Verificado'
);

-- =========================
-- INCIDENCIAS
-- =========================

INSERT INTO Incidencia (
    id,
    orden_id,
    usuario_registro_id,
    usuario_gestion_id,
    tipo_incidencia_id,
    descripcion,
    fecha_incidente,
    ley_aplicada,
    acciones_tomadas,
    resuelto
) VALUES
(
    1,
    1,
    1,
    2,
    1,
    'Demora por trafico',
    CURRENT_TIMESTAMP,
    'Ley de Transito',
    'Se notifico al cliente',
    1
);

-- =========================
-- AUDITORIA
-- =========================

INSERT INTO Auditoria_Estado (
    id,
    orden_id,
    estado_anterior_id,
    estado_nuevo_id,
    fecha_cambio,
    solicitante_id,
    confirmador_id,
    motivo
) VALUES
(
    1,
    1,
    1,
    2,
    CURRENT_TIMESTAMP,
    1,
    2,
    'Salida de planta confirmada'
);