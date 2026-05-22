-- Script de inicialización de base de datos para StudyFlow
-- Ejecutar en PostgreSQL

CREATE TABLE IF NOT EXISTS usuarios (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255),
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_conexion TIMESTAMP,
    algoritmo_default VARCHAR(50) DEFAULT 'SM2',
    tarjetas_por_sesion INT DEFAULT 10,
    idioma VARCHAR(10) DEFAULT 'es',
    tema VARCHAR(20) DEFAULT 'light',
    notificaciones_habilitadas BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS mazos (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    etiquetas TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_modificacion TIMESTAMP,
    algoritmo VARCHAR(50) DEFAULT 'SM2',
    publicado BOOLEAN DEFAULT FALSE,
    total_tarjetas INT DEFAULT 0,
    usuario_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_mazos_usuarios FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tarjetas (
    id VARCHAR(36) PRIMARY KEY,
    frente TEXT NOT NULL,
    reverso TEXT NOT NULL,
    etiquetas TEXT,
    tipo VARCHAR(50) DEFAULT 'TEXTO',
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    proximo_repaso TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nivel_dificultad INT DEFAULT 0,
    intentos_totales INT DEFAULT 0,
    aciertos INT DEFAULT 0,
    con_pista BOOLEAN DEFAULT FALSE,
    pista TEXT,
    mazo_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_tarjetas_mazos FOREIGN KEY (mazo_id) REFERENCES mazos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sesiones (
    id VARCHAR(36) PRIMARY KEY,
    mazo_id VARCHAR(36) NOT NULL,
    usuario_id VARCHAR(36) NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_fin TIMESTAMP,
    tarjeta_actual_index INT DEFAULT 0,
    completada BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_sesiones_mazos FOREIGN KEY (mazo_id) REFERENCES mazos(id) ON DELETE CASCADE,
    CONSTRAINT fk_sesiones_usuarios FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS registros_respuesta (
    id VARCHAR(36) PRIMARY KEY,
    tarjeta_id VARCHAR(36) NOT NULL,
    sesion_id VARCHAR(36) NOT NULL,
    calificacion VARCHAR(50) NOT NULL,
    tiempo_respuesta INT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_registros_tarjetas FOREIGN KEY (tarjeta_id) REFERENCES tarjetas(id) ON DELETE CASCADE,
    CONSTRAINT fk_registros_sesiones FOREIGN KEY (sesion_id) REFERENCES sesiones(id) ON DELETE CASCADE
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_mazos_usuario ON mazos(usuario_id);
CREATE INDEX idx_tarjetas_mazo ON tarjetas(mazo_id);
CREATE INDEX idx_tarjetas_proximo_repaso ON tarjetas(proximo_repaso);
CREATE INDEX idx_sesiones_mazo ON sesiones(mazo_id);
CREATE INDEX idx_sesiones_usuario ON sesiones(usuario_id);
CREATE INDEX idx_registros_sesion ON registros_respuesta(sesion_id);
CREATE INDEX idx_registros_tarjeta ON registros_respuesta(tarjeta_id);
