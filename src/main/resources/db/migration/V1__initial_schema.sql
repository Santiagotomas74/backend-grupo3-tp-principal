-- =========================
-- TABLAS MAESTRAS
-- =========================

CREATE TABLE Provincia (
    id INTEGER PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE Localidad (
    id INTEGER PRIMARY KEY,
    provincia_id INTEGER NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    codigo_postal VARCHAR(20),

    FOREIGN KEY (provincia_id) REFERENCES Provincia(id)
);

CREATE TABLE Tipo_Lugar_Operativo (
    id INTEGER PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE
);

CREATE TABLE Estado_Vehiculo (
    id INTEGER PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE
);

CREATE TABLE Tipo_Vinculo (
    id INTEGER PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE
);

CREATE TABLE Estado_Orden_Carga (
    id INTEGER PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE
);

CREATE TABLE Tipo_Incidencia (
    id INTEGER PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE
);

CREATE TABLE Tipo_Documento (
    id INTEGER PRIMARY KEY,
    nombre VARCHAR(255),
    categoria TEXT
);

CREATE TABLE Rol (
    id INTEGER PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    descripcion TEXT
);

CREATE TABLE Permiso (
    id INTEGER PRIMARY KEY,
    codigo VARCHAR(255) NOT NULL UNIQUE,
    descripcion TEXT
);

CREATE TABLE Combustible (
    id INTEGER PRIMARY KEY,

    nombre VARCHAR(255) UNIQUE,

    numero_onu VARCHAR(50),
    clase_riesgo VARCHAR(50),

    densidad DECIMAL(10,4),
    temperatura_referencia DECIMAL(10,4)
);

-- =========================
-- LUGARES OPERATIVOS
-- =========================

CREATE TABLE Lugar_Operativo (
    id INTEGER PRIMARY KEY,

    tipo_id INTEGER,

    nombre VARCHAR(255) NOT NULL,

    direccion VARCHAR(255),

    localidad_id INTEGER,

    latitud DECIMAL(10,7),
    longitud DECIMAL(10,7),

    puede_recibir BOOLEAN DEFAULT 0,
    puede_despachar BOOLEAN DEFAULT 0,

    activo BOOLEAN NOT NULL DEFAULT 1,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    FOREIGN KEY (tipo_id) REFERENCES Tipo_Lugar_Operativo(id),
    FOREIGN KEY (localidad_id) REFERENCES Localidad(id)
);

-- =========================
-- USUARIOS / ROLES
-- =========================

CREATE TABLE Usuario (
    id INTEGER PRIMARY KEY,

    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,

    dni BIGINT NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    legajo VARCHAR(255) NOT NULL UNIQUE,

    password_hash VARCHAR(255) NOT NULL,

    activo BOOLEAN NOT NULL DEFAULT 1,

    lugar_operativo_id INTEGER,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    FOREIGN KEY (lugar_operativo_id) REFERENCES Lugar_Operativo(id)
);

CREATE TABLE Usuario_Rol (
    usuario_id INTEGER NOT NULL,
    rol_id INTEGER NOT NULL,

    PRIMARY KEY (usuario_id, rol_id),

    FOREIGN KEY (usuario_id) REFERENCES Usuario(id),
    FOREIGN KEY (rol_id) REFERENCES Rol(id)
);

CREATE TABLE Rol_Permiso (
    rol_id INTEGER NOT NULL,
    permiso_id INTEGER NOT NULL,

    PRIMARY KEY (rol_id, permiso_id),

    FOREIGN KEY (rol_id) REFERENCES Rol(id),
    FOREIGN KEY (permiso_id) REFERENCES Permiso(id)
);

-- =========================
-- EMPRESAS / TRANSPORTISTAS
-- =========================

CREATE TABLE Empresa_Tercerizada (
    id INTEGER PRIMARY KEY,

    nombre_fantasia VARCHAR(255),
    razon_social VARCHAR(255) NOT NULL,

    cuit VARCHAR(50) NOT NULL UNIQUE,

    direccion VARCHAR(255),

    localidad_id INTEGER,

    telefono VARCHAR(100),

    activo BOOLEAN DEFAULT 1,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    FOREIGN KEY (localidad_id) REFERENCES Localidad(id)
);

CREATE TABLE Transportista (
    id INTEGER PRIMARY KEY,

    usuario_id INTEGER UNIQUE,

    tipo_vinculo_id INTEGER,

    cuit VARCHAR(50) NOT NULL UNIQUE,

    empresa_id INTEGER,

    activo BOOLEAN DEFAULT 1,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    FOREIGN KEY (usuario_id) REFERENCES Usuario(id),
    FOREIGN KEY (tipo_vinculo_id) REFERENCES Tipo_Vinculo(id),
    FOREIGN KEY (empresa_id) REFERENCES Empresa_Tercerizada(id)
);

-- =========================
-- VEHICULOS
-- =========================

CREATE TABLE Vehiculo (
    id INTEGER PRIMARY KEY,

    patente VARCHAR(20) NOT NULL UNIQUE,

    empresa_id INTEGER,

    capacidad_total_litros DECIMAL(12,2),

    marca VARCHAR(255),
    modelo VARCHAR(255),

    estado_id INTEGER,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    FOREIGN KEY (empresa_id) REFERENCES Empresa_Tercerizada(id),
    FOREIGN KEY (estado_id) REFERENCES Estado_Vehiculo(id)
);

CREATE TABLE Acoplado (
    id INTEGER PRIMARY KEY,

    patente VARCHAR(20) NOT NULL UNIQUE,

    capacidad_maxima_litros DECIMAL(12,2),

    empresa_id INTEGER,


    estado_id INTEGER,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    FOREIGN KEY (empresa_id) REFERENCES Empresa_Tercerizada(id),
    FOREIGN KEY (estado_id) REFERENCES Estado_Vehiculo(id)
);

-- =========================
-- ORDENES DE CARGA
-- =========================

CREATE TABLE Orden_Carga (
    id INTEGER PRIMARY KEY,

    numero_remito VARCHAR(255) NOT NULL UNIQUE,
    cot VARCHAR(255) NOT NULL UNIQUE,

    camion_id INTEGER,
    acoplado_id INTEGER,

    transportista_id INTEGER,

    planta_despacho_id INTEGER,
    estacion_destino_id INTEGER,

    operador_id INTEGER,

    combustible_id INTEGER,

    estado_id INTEGER,



fecha_creacion TIMESTAMP,
fecha_modificacion TIMESTAMP,
fecha_salida_planta TIMESTAMP,
fecha_entrega_estimada TIMESTAMP,
fecha_entrega_real TIMESTAMP,

    temperatura_carga DECIMAL(10,4),
    densidad_carga DECIMAL(10,4),

    litros_cargados DECIMAL(12,2),
    litros_entregados DECIMAL(12,2),

    fie_adjunta BOOLEAN DEFAULT 0,

    observaciones TEXT,

    FOREIGN KEY (camion_id) REFERENCES Vehiculo(id),
    FOREIGN KEY (acoplado_id) REFERENCES Acoplado(id),
    FOREIGN KEY (transportista_id) REFERENCES Transportista(id),
    FOREIGN KEY (planta_despacho_id) REFERENCES Lugar_Operativo(id),
    FOREIGN KEY (estacion_destino_id) REFERENCES Lugar_Operativo(id),
    FOREIGN KEY (operador_id) REFERENCES Usuario(id),
    FOREIGN KEY (combustible_id) REFERENCES Combustible(id),
    FOREIGN KEY (estado_id) REFERENCES Estado_Orden_Carga(id)
);

-- =========================
-- DOCUMENTACION
-- =========================

CREATE TABLE Documentacion (
    id INTEGER PRIMARY KEY,

    tipo_documento_id INTEGER,

    transportista_id INTEGER,
    camion_id INTEGER,
    acoplado_id INTEGER,

    nro_documento VARCHAR(255),

    fecha_emision DATE,
    fecha_vencimiento DATE,

    archivo_url VARCHAR(500),

    estado_verificacion BOOLEAN DEFAULT 0,

    comentarios TEXT,

    FOREIGN KEY (tipo_documento_id) REFERENCES Tipo_Documento(id),
    FOREIGN KEY (transportista_id) REFERENCES Transportista(id),
    FOREIGN KEY (camion_id) REFERENCES Vehiculo(id),
    FOREIGN KEY (acoplado_id) REFERENCES Acoplado(id),

    CHECK (
        transportista_id IS NOT NULL OR
        camion_id IS NOT NULL OR
        acoplado_id IS NOT NULL
    )
);

-- =========================
-- INCIDENCIAS
-- =========================

CREATE TABLE Incidencia (
    id INTEGER PRIMARY KEY,

    orden_id INTEGER,

    usuario_registro_id INTEGER,

    usuario_gestion_id INTEGER,

    tipo_incidencia_id INTEGER,

    descripcion TEXT,

    fecha_incidente TIMESTAMP,

    ley_aplicada VARCHAR(255),

    acciones_tomadas TEXT,

    resuelto BOOLEAN DEFAULT 0,

    fecha_resolucion TIMESTAMP,

    FOREIGN KEY (orden_id) REFERENCES Orden_Carga(id),
    FOREIGN KEY (usuario_registro_id) REFERENCES Usuario(id),
    FOREIGN KEY (usuario_gestion_id) REFERENCES Usuario(id),
    FOREIGN KEY (tipo_incidencia_id) REFERENCES Tipo_Incidencia(id)
);

-- =========================
-- AUDITORIA
-- =========================

CREATE TABLE Auditoria_Estado (
    id INTEGER PRIMARY KEY,

    orden_id INTEGER,

    estado_anterior_id INTEGER,
    estado_nuevo_id INTEGER,

    fecha_cambio TIMESTAMP,

    solicitante_id INTEGER,

    confirmador_id INTEGER,

    motivo TEXT,

    FOREIGN KEY (orden_id) REFERENCES Orden_Carga(id),
    FOREIGN KEY (estado_anterior_id) REFERENCES Estado_Orden_Carga(id),
    FOREIGN KEY (estado_nuevo_id) REFERENCES Estado_Orden_Carga(id),
    FOREIGN KEY (solicitante_id) REFERENCES Usuario(id),
    FOREIGN KEY (confirmador_id) REFERENCES Usuario(id)
);

-- =========================
-- INDICES EXTRA
-- =========================

CREATE INDEX idx_usuario_lugar_operativo
ON Usuario(lugar_operativo_id);

CREATE INDEX idx_transportista_empresa
ON Transportista(empresa_id);

CREATE INDEX idx_vehiculo_empresa
ON Vehiculo(empresa_id);

CREATE INDEX idx_acoplado_empresa
ON Acoplado(empresa_id);

CREATE INDEX idx_orden_estado
ON Orden_Carga(estado_id);

CREATE INDEX idx_orden_transportista
ON Orden_Carga(transportista_id);

CREATE INDEX idx_documentacion_vencimiento
ON Documentacion(fecha_vencimiento);

CREATE INDEX idx_incidencia_orden
ON Incidencia(orden_id);

CREATE INDEX idx_auditoria_orden
ON Auditoria_Estado(orden_id);