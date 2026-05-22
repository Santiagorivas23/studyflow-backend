-- Flyway initial schema for StudyFlow
-- Tables: usuarios, mazos, tarjetas, sesiones, registros_respuesta

CREATE TABLE usuarios (
    id VARCHAR(255) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    fecha_registro TIMESTAMP NOT NULL,
    ultima_conexion TIMESTAMP,
    idioma VARCHAR(255),
    algoritmo_default VARCHAR(255),
    notificaciones_habilitadas BOOLEAN,
    tarjetas_por_sesion INTEGER,
    tema VARCHAR(255)
);

CREATE TABLE mazos (
    id VARCHAR(255) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255),
    etiquetas VARCHAR(255),
    fecha_creacion TIMESTAMP NOT NULL,
    ultima_modificacion TIMESTAMP,
    publicado BOOLEAN,
    total_tarjetas INTEGER,
    algoritmo VARCHAR(255),
    usuario_id VARCHAR(255) NOT NULL
);

CREATE TABLE tarjetas (
    id VARCHAR(255) PRIMARY KEY,
    frente TEXT NOT NULL,
    reverso TEXT NOT NULL,
    pista TEXT,
    con_pista BOOLEAN,
    etiquetas VARCHAR(255),
    fecha_creacion TIMESTAMP NOT NULL,
    proximo_repaso TIMESTAMP NOT NULL,
    nivel_dificultad INTEGER,
    aciertos INTEGER,
    intentos_totales INTEGER,
    tipo VARCHAR(255),
    mazo_id VARCHAR(255) NOT NULL
);

CREATE TABLE sesiones (
    id VARCHAR(255) PRIMARY KEY,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP,
    tarjeta_actual_index INTEGER,
    completada BOOLEAN,
    mazo_id VARCHAR(255) NOT NULL,
    usuario_id VARCHAR(255) NOT NULL
);

CREATE TABLE registros_respuesta (
    id VARCHAR(255) PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    tiempo_respuesta INTEGER,
    calificacion VARCHAR(255),
    sesion_id VARCHAR(255) NOT NULL,
    tarjeta_id VARCHAR(255) NOT NULL
);

-- Foreign keys
ALTER TABLE mazos
  ADD CONSTRAINT fk_mazos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id);

ALTER TABLE tarjetas
  ADD CONSTRAINT fk_tarjetas_mazo FOREIGN KEY (mazo_id) REFERENCES mazos(id);

ALTER TABLE sesiones
  ADD CONSTRAINT fk_sesiones_mazo FOREIGN KEY (mazo_id) REFERENCES mazos(id);

ALTER TABLE sesiones
  ADD CONSTRAINT fk_sesiones_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id);

ALTER TABLE registros_respuesta
  ADD CONSTRAINT fk_registros_sesion FOREIGN KEY (sesion_id) REFERENCES sesiones(id);

ALTER TABLE registros_respuesta
  ADD CONSTRAINT fk_registros_tarjeta FOREIGN KEY (tarjeta_id) REFERENCES tarjetas(id);
